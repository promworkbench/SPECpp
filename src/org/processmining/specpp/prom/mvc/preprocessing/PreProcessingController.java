package org.processmining.specpp.prom.mvc.preprocessing;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections4.BidiMap;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.specpp.config.DataExtractionParameters;
import org.processmining.specpp.config.InputProcessingConfig;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.ParsedLog;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.util.Tuple3;
import org.processmining.specpp.config.BaseDataExtractionStrategy;
import org.processmining.specpp.config.PreProcessingParameters;
import org.processmining.specpp.preprocessing.InputDataBundle;
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
    private SwingWorker<Tuple3<InputProcessingConfig, Pair<Set<Activity>>, InputDataBundle>, Void> applicationWorker;

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


    private InputProcessingConfig lastDataConfig;
    private PreProcessingParameters lastPreProcessingParameters;
    private DataExtractionParameters lastExtractionParameters;
    private Pair<Comparator<Activity>> lastComparators;
    private ParsedLog lastDerivedLog;

    public void preview(InputProcessingConfig collectedParameters) {
        previewWorker(collectedParameters);
    }


    protected SwingWorker<Pair<Comparator<Activity>>, Void> previewWorker(InputProcessingConfig collectedParameters) {
        if (preprocessingWorker != null && !preprocessingWorker.isDone()) preprocessingWorker.cancel(true);
        preprocessingWorker = new SwingWorker<Pair<Comparator<Activity>>, Void>() {

            @Override
            protected Pair<Comparator<Activity>> doInBackground() throws Exception {
                parametersPanel.disableButton();
                previewPanel.disableButton();


                PreProcessingParameters preProcessingParameters = collectedParameters.getPreProcessingParameters();
                if (lastDataConfig == null || !lastDataConfig.getPreProcessingParameters()
                                                             .equals(preProcessingParameters)) {
                    lastDerivedLog = collectedParameters.getParsedLogDataSource(rawLog).getData();
                    lastPreProcessingParameters = preProcessingParameters;
                }
                Pair<Comparator<Activity>> orderings = BaseDataExtractionStrategy.createOrderings(lastDerivedLog.getLog(), lastDerivedLog.getStringActivityMapping(), collectedParameters.getDataExtractionParameters()
                                                                                                                                                                                         .getActivityOrderingStrategy());

                lastDataConfig = collectedParameters;
                lastComparators = orderings;
                return orderings;
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        Pair<Comparator<Activity>> comparators = get();
                        Collection<Activity> activities = lastDerivedLog.getStringActivityMapping().values();
                        previewPanel.updateLists(activities, comparators);
                        variantPanel.updateLog(lastDerivedLog.getLog());
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

    public SwingWorker<Tuple3<InputProcessingConfig, Pair<Set<Activity>>, InputDataBundle>, Void> applyWorker(Pair<Set<Activity>> selectedActivities) {
        if (applicationWorker != null && !applicationWorker.isDone()) applicationWorker.cancel(true);
        applicationWorker = new SwingWorker<Tuple3<InputProcessingConfig, Pair<Set<Activity>>, InputDataBundle>, Void>() {

            @Override
            protected Tuple3<InputProcessingConfig, Pair<Set<Activity>>, InputDataBundle> doInBackground() throws Exception {
                Pair<Set<Activity>> selection = selectedActivities;

                previewPanel.disableButton();
                InputProcessingConfig collectedParameters = parametersPanel.collectParameters();
                if (lastDataConfig == null || !lastDataConfig.equals(collectedParameters)) {
                    SwingWorker<Pair<Comparator<Activity>>, Void> w = previewWorker(collectedParameters);
                    Pair<Comparator<Activity>> comparators = w.get();
                    if (w.isCancelled()) throw new RuntimeException("data dependency failed to compute");
                    Collection<Activity> activities = lastDerivedLog.getStringActivityMapping().values();
                    SwingWorker<Pair<List<Activity>>, Void> lists = previewPanel.updateLists(activities, comparators);
                    selection = ImmutablePair.map(lists.get(), HashSet::new);
                }
                BidiMap<Activity, Transition> transitionMapping = BaseDataExtractionStrategy.createTransitions(lastDerivedLog.getLog(), lastDerivedLog.getStringActivityMapping());
                IntEncodings<Transition> encodings = ActivityOrderingStrategy.createEncodings(selection, lastComparators, transitionMapping);
                return new Tuple3<>(lastDataConfig, selection, new InputDataBundle(lastDerivedLog.getLog(), encodings, transitionMapping));
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        Tuple3<InputProcessingConfig, Pair<Set<Activity>>, InputDataBundle> tuple3 = get();
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
