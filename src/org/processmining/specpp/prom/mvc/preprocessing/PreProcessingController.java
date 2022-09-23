package org.processmining.specpp.prom.mvc.preprocessing;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections4.BidiMap;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.datastructures.util.Tuple3;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.preprocessing.XLogBasedInputDataBundle;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingStrategy;
import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class PreProcessingController extends AbstractStageController {

    private final XLog rawLog;
    private ParametersPanel parametersPanel;
    private PreviewPanel previewPanel;
    private VariantPanel variantPanel;
    private SwingWorker<Pair<Comparator<Activity>>, Void> preprocessingWorker;
    private SwingWorker<Tuple3<PreProcessingParameters, Pair<Set<Activity>>, InputDataBundle>, Void> applicationWorker;

    public PreProcessingController(SPECppController parentController) {
        super(parentController);
        this.rawLog = parentController.getRawLog();
    }

    public PreProcessingPanel createPreProcessingPanel() {
        variantPanel = createVariantVisualizationPanel();
        parametersPanel = createParametersPanel();
        previewPanel = createPreviewPanel();
        return new PreProcessingPanel(this, variantPanel, parametersPanel, previewPanel);
    }

    public VariantPanel createVariantVisualizationPanel() {
        return new VariantPanel();
    }

    public ParametersPanel createParametersPanel() {
        List<XEventClassifier> classifiers = rawLog.getClassifiers();
        return new ParametersPanel(this, classifiers.isEmpty() ? ImmutableList.of(new XEventNameClassifier()) : classifiers);
    }

    public PreviewPanel createPreviewPanel() {
        return new PreviewPanel(this);
    }


    private PreProcessingParameters lastParameters;
    private Pair<Comparator<Activity>> lastComparators;
    private Tuple2<Log, Map<String, Activity>> lastDerivedLog;

    public void preview(PreProcessingParameters collectedParameters) {
        previewWorker(collectedParameters);
    }


    protected SwingWorker<Pair<Comparator<Activity>>, Void> previewWorker(PreProcessingParameters collectedParameters) {
        if (preprocessingWorker != null && !preprocessingWorker.isDone()) preprocessingWorker.cancel(true);
        preprocessingWorker = new SwingWorker<Pair<Comparator<Activity>>, Void>() {

            @Override
            protected Pair<Comparator<Activity>> doInBackground() throws Exception {
                parametersPanel.disableButton();
                previewPanel.disableButton();

                if (lastParameters == null || lastParameters.isAddStartEndTransitions() != collectedParameters.isAddStartEndTransitions() || !lastParameters.getEventClassifier()
                                                                                                                                                            .equals(collectedParameters.getEventClassifier())) {
                    lastDerivedLog = XLogBasedInputDataBundle.convertLog(rawLog, collectedParameters.getEventClassifier(), collectedParameters.isAddStartEndTransitions());
                    lastParameters = collectedParameters;
                }
                Pair<Comparator<Activity>> comparators = XLogBasedInputDataBundle.createOrderings(lastDerivedLog.getT1(), lastDerivedLog.getT2(), collectedParameters.getTransitionEncodingsBuilderClass());
                lastComparators = comparators;
                return comparators;
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        Pair<Comparator<Activity>> comparators = get();
                        Collection<Activity> activities = lastDerivedLog.getT2().values();
                        previewPanel.updateLists(activities, comparators);
                        variantPanel.updateLog(lastDerivedLog.getT1());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                } finally {
                    parametersPanel.enableButton();
                    previewPanel.enableButton();
                }
            }
        };
        preprocessingWorker.execute();
        return preprocessingWorker;
    }


    public void apply(Pair<Set<Activity>> selectedActivities) {
        applyWorker(selectedActivities);
    }

    public SwingWorker<Tuple3<PreProcessingParameters, Pair<Set<Activity>>, InputDataBundle>, Void> applyWorker(Pair<Set<Activity>> selectedActivities) {
        if (applicationWorker != null && !applicationWorker.isDone()) applicationWorker.cancel(true);
        applicationWorker = new SwingWorker<Tuple3<PreProcessingParameters, Pair<Set<Activity>>, InputDataBundle>, Void>() {

            @Override
            protected Tuple3<PreProcessingParameters, Pair<Set<Activity>>, InputDataBundle> doInBackground() throws Exception {
                Pair<Set<Activity>> selection = selectedActivities;

                previewPanel.disableButton();
                PreProcessingParameters collectedParameters = parametersPanel.collectParameters();
                if (lastParameters == null || !lastParameters.equals(collectedParameters)) {
                    SwingWorker<Pair<Comparator<Activity>>, Void> w = previewWorker(collectedParameters);
                    Pair<Comparator<Activity>> comparators = w.get();
                    if (w.isCancelled()) throw new RuntimeException("data dependency failed to compute");
                    Collection<Activity> activities = lastDerivedLog.getT2().values();
                    SwingWorker<Pair<List<Activity>>, Void> lists = previewPanel.updateLists(activities, comparators);
                    selection = ImmutablePair.map(lists.get(), HashSet::new);
                }
                BidiMap<Activity, Transition> transitionMapping = XLogBasedInputDataBundle.createTransitions(lastDerivedLog.getT1(), lastDerivedLog.getT2());
                IntEncodings<Transition> encodings = ActivityOrderingStrategy.createEncodings(selection, lastComparators, transitionMapping);
                return new Tuple3<>(lastParameters, selection, new InputDataBundle(lastDerivedLog.getT1(), encodings, transitionMapping));
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        Tuple3<PreProcessingParameters, Pair<Set<Activity>>, InputDataBundle> tuple3 = get();
                        parentController.preprocessingCompleted(tuple3.getT1(), tuple3.getT2(), tuple3.getT3());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    previewPanel.enableButton();
                }

            }
        };
        applicationWorker.execute();
        return applicationWorker;
    }

    @Override
    public JPanel createPanel() {
        return createPreProcessingPanel();
    }

    @Override
    public void startup() {

    }
}
