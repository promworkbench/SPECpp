package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.utils.XUtils;
import org.processmining.modelrepair.plugins.align.CostBasedCompleteParamProvider_nonUI;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.etconformance.ETCAlgorithm;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.pnetreplayer.utils.TransEvClassMappingUtils;
import org.processmining.specpp.datastructures.log.impls.Factory;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.supervision.supervisors.DebuggingSupervisor;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ResultEvaluationPanel extends AbstractStagePanel<ResultController> {

    private final JLabel fitnessLabel, precisionLabel;
    private final SwingWorker<ETCResults, Void> precisionWorker;
    private final SwingWorker<Double, Void> fitnessWorker;

    public ResultEvaluationPanel(ResultController resultController, ProMPetrinetWrapper proMPetrinetWrapper) {
        super(resultController);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(SlickerFactory.instance()
                          .createLabel(String.format("Petri net contains %d transitions, %d places & %d arcs.", proMPetrinetWrapper.getTransitions()
                                                                                                                                   .size(), proMPetrinetWrapper.getPlaces()
                                                                                                                                                               .size(), proMPetrinetWrapper.getEdges()
                                                                                                                                                                                           .size())));
        add(Box.createHorizontalStrut(10));
        fitnessLabel = SlickerFactory.instance().createLabel("Alignment-Based Fitness: ?");
        add(fitnessLabel);
        add(Box.createHorizontalStrut(10));
        precisionLabel = SlickerFactory.instance().createLabel("ETC Precision: ?");
        add(precisionLabel);

        XLog evalLog;
        if (resultController.getParentController().getPreProcessingParameters().isAddStartEndTransitions()) {
            XFactoryNaiveImpl xFactorY = new XFactoryNaiveImpl();
            XAttributeMap attributeMap = xFactorY.createAttributeMap();
            attributeMap.put("concept:name", xFactorY.createAttributeLiteral("concept:name", Factory.UNIQUE_START_LABEL, XConceptExtension.instance()));
            XEvent startEvent = xFactorY.createEvent(attributeMap);
            attributeMap = xFactorY.createAttributeMap();
            attributeMap.put("concept:name", xFactorY.createAttributeLiteral("concept:name", Factory.UNIQUE_END_LABEL, XConceptExtension.instance()));
            XEvent endEvent = xFactorY.createEvent(attributeMap);
            XLog copiedLog = XUtils.cloneLogWithoutGlobalsAndClassifiers(resultController.getRawLog());
            for (XTrace trace : copiedLog) {
                trace.add(0, startEvent);
                trace.add(trace.size() - 1, endEvent);
            }
            evalLog = copiedLog;
        } else evalLog = resultController.getRawLog();

        fitnessWorker = new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                TransEvClassMapping mapping = getTransEvClassMapping(evalLog, resultController.getEventClassifier(), proMPetrinetWrapper);
                PluginContext context = resultController.getContext().createChildContext("Fitness");
                CostBasedCompleteParamProvider_nonUI provider = new CostBasedCompleteParamProvider_nonUI(context, proMPetrinetWrapper, evalLog, mapping);
                IPNReplayParameter params = provider.constructReplayParameter(provider.getDefaultEventCost(), provider.getDefaultTransitionCost(), provider.getDefaultNumOfStates());
                PNRepResult syncReplayResults = new PNLogReplayer().replayLog(context, proMPetrinetWrapper, evalLog, mapping, new PetrinetReplayerWithoutILP(), params);
                return (Double) syncReplayResults.getInfo().get(PNRepResult.TRACEFITNESS);
            }

            @Override
            protected void done() {
                try {
                    Double o = get();
                    fitnessLabel.setText(String.format("Alignment-Based Fitness: %.2f", o));
                } catch (InterruptedException | ExecutionException ignored) {
                    fitnessLabel.setText("Alignment-Based Fitness: failed");
                    DebuggingSupervisor.debug("Result Evaluation", "Alignment-Based Fitness failed:\n");
                    ignored.printStackTrace();
                }
            }
        };


        precisionWorker = new SwingWorker<ETCResults, Void>() {

            @Override
            protected ETCResults doInBackground() throws Exception {
                TransEvClassMapping transEvClassMapping = getTransEvClassMapping(evalLog, resultController.getEventClassifier(), proMPetrinetWrapper);
                PluginContext childContext = resultController.getContext().createChildContext("Precision");
                ETCResults etcResults = new ETCResults();
                ETCAlgorithm.exec(childContext, evalLog, proMPetrinetWrapper, proMPetrinetWrapper.getInitialMarking(), transEvClassMapping, etcResults);
                return etcResults;
            }

            @Override
            protected void done() {
                try {
                    ETCResults etcResults = get();
                    precisionLabel.setText(String.format("ETC Precision: %.2f", etcResults.getEtcp()));
                } catch (InterruptedException | ExecutionException ignored) {
                    precisionLabel.setText("ETC Precision: failed");
                    DebuggingSupervisor.debug("Result Evaluation", "ETC Precision failed:\n");
                    ignored.printStackTrace();
                }
            }
        };

        fitnessWorker.execute();
        precisionWorker.execute();

        // 2 min
        Timer timer = new Timer(2 * 60 * 1000, e -> killWorkers());
        timer.setRepeats(false);
        timer.start();
    }

    private void killWorkers() {
        fitnessWorker.cancel(true);
        precisionWorker.cancel(true);
    }

    private static TransEvClassMapping getTransEvClassMapping(XLog xLog, XEventClassifier eventClassifier, ProMPetrinetWrapper proMPetrinetWrapper) {
        Set<XEventClass> eventClasses = new HashSet<>(XUtils.createEventClasses(eventClassifier, xLog).getClasses());
        return TransEvClassMappingUtils.getInstance().getMapping(proMPetrinetWrapper, eventClasses, eventClassifier);
    }

    @Override
    public void destroy() {
        killWorkers();
    }
}
