package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.utils.XUtils;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.etconformance.ETCAlgorithm;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.pnetreplayer.utils.TransEvClassMappingUtils;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.supervision.supervisors.DebuggingSupervisor;

import javax.swing.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

        XLog evalLog = resultController.getEvalLog();

        fitnessWorker = new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                Tuple2<TransEvClassMapping, Set<XEventClass>> tuple = getTransEvClassMapping(evalLog, resultController.getEventClassifier(), proMPetrinetWrapper);
                PluginContext context = resultController.getContext().createChildContext("Fitness");
                Map<XEventClass, Integer> mapEvClass2Cost = tuple.getT2()
                                                                 .stream()
                                                                 .collect(Collectors.toMap(a -> a, a -> 5));
                mapEvClass2Cost.put(tuple.getT1().getDummyEventClass(), 5);
                Map<Transition, Integer> mapTrans2Cost = proMPetrinetWrapper.getTransitions()
                                                                            .stream()
                                                                            .collect(Collectors.toMap(t -> t, t -> 2));
                CostBasedCompleteParam paramObj = new CostBasedCompleteParam(mapEvClass2Cost, mapTrans2Cost);
                paramObj.setMaxNumOfStates(Integer.MAX_VALUE);
                paramObj.setInitialMarking(proMPetrinetWrapper.getInitialMarking());
                paramObj.setFinalMarkings(proMPetrinetWrapper.getFinalMarkings().toArray(new Marking[0]));
                paramObj.setGUIMode(false);
                paramObj.setCreateConn(false);
                PNRepResult syncReplayResults = new PNLogReplayer().replayLog(context, proMPetrinetWrapper, evalLog, tuple.getT1(), new PetrinetReplayerWithoutILP(), paramObj);
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
                TransEvClassMapping transEvClassMapping = getTransEvClassMapping(evalLog, resultController.getEventClassifier(), proMPetrinetWrapper).getT1();
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

        // 2 min timeout
        Timer timer = new Timer(2 * 60 * 1000, e -> killWorkers());
        timer.setRepeats(false);
        timer.start();
    }

    private void killWorkers() {
        fitnessWorker.cancel(true);
        precisionWorker.cancel(true);
    }

    private static Tuple2<TransEvClassMapping, Set<XEventClass>> getTransEvClassMapping(XLog xLog, XEventClassifier eventClassifier, ProMPetrinetWrapper proMPetrinetWrapper) {
        Set<XEventClass> eventClasses = new HashSet<>(XUtils.createEventClasses(eventClassifier, xLog).getClasses());
        return new ImmutableTuple2<>(TransEvClassMappingUtils.getInstance()
                                                             .getMapping(proMPetrinetWrapper, eventClasses, eventClassifier), eventClasses);
    }

    @Override
    public void destroy() {
        killWorkers();
    }
}
