package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.supervision.supervisors.DebuggingSupervisor;
import org.processmining.specpp.util.EvalUtils;

import javax.swing.*;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ResultEvaluationPanel extends AbstractStagePanel<ResultController> {

    private final JLabel fitnessLabel, precisionLabel;
    private SwingWorker<Double, Void> precisionWorker;
    private SwingWorker<Double, Void> fitnessWorker;
    private final SwingWorker<Tuple2<EvalUtils.EvaluationLogData, TransEvClassMapping>, Void> dataWorker;
    private final Timer timer;

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

        dataWorker = new SwingWorker<Tuple2<EvalUtils.EvaluationLogData, TransEvClassMapping>, Void>() {

            @Override
            protected Tuple2<EvalUtils.EvaluationLogData, TransEvClassMapping> doInBackground() throws Exception {
                XLog evalLog = resultController.createEvalLog();
                XEventClassifier eventClassifier = resultController.getEventClassifier();
                Set<XEventClass> eventClasses = EvalUtils.createEventClasses(eventClassifier, evalLog);
                return new ImmutableTuple2<>(new EvalUtils.EvaluationLogData(evalLog, eventClassifier, eventClasses), EvalUtils.createTransEvClassMapping(eventClassifier, evalLog, proMPetrinetWrapper));
            }

            @Override
            protected void done() {
                try {
                    Tuple2<EvalUtils.EvaluationLogData, TransEvClassMapping> tuple2 = get();
                    EvalUtils.EvaluationLogData evaluationLogData = tuple2.getT1();
                    TransEvClassMapping evClassMapping = tuple2.getT2();
                    fitnessWorker = new SwingWorker<Double, Void>() {
                        @Override
                        protected Double doInBackground() throws Exception {
                            PluginContext context = resultController.getContext().createChildContext("Fitness");
                            return EvalUtils.computeAlignmentBasedFitness(context, evaluationLogData, evClassMapping, proMPetrinetWrapper);
                        }

                        @Override
                        protected void done() {
                            try {
                                Double o = get();
                                fitnessLabel.setText(String.format("Alignment-Based Fitness: %.2f", o));
                            } catch (InterruptedException | ExecutionException e) {
                                fitnessLabel.setText("Alignment-Based Fitness: failed");
                                e.fillInStackTrace();
                                DebuggingSupervisor.debug("Result Evaluation", "Alignment-Based Fitness failed:\n" + e);
                            }
                        }
                    };

                    precisionWorker = new SwingWorker<Double, Void>() {

                        @Override
                        protected Double doInBackground() throws Exception {
                            PluginContext childContext = resultController.getContext().createChildContext("Precision");
                            return EvalUtils.computeETCPrecision(childContext, evaluationLogData, evClassMapping, proMPetrinetWrapper);
                        }

                        @Override
                        protected void done() {
                            try {
                                Double etcPrecision = get();
                                precisionLabel.setText(String.format("ETC Precision: %.2f", etcPrecision));
                            } catch (InterruptedException | ExecutionException e) {
                                precisionLabel.setText("ETC Precision: failed");
                                e.fillInStackTrace();
                                DebuggingSupervisor.debug("Result Evaluation", "ETC Precision failed:\n" + e);
                            }
                        }
                    };

                    fitnessWorker.execute();
                    precisionWorker.execute();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        dataWorker.execute();

        // 2 min timeout
        timer = new Timer(2 * 60 * 1000, e -> killWorkers());
        timer.setRepeats(false);
        timer.start();
    }

    private void killWorkers() {
        if (timer != null && timer.isRunning()) timer.stop();
        if (fitnessWorker != null && !fitnessWorker.isDone()) fitnessWorker.cancel(true);
        if (precisionWorker != null && !precisionWorker.isDone()) precisionWorker.cancel(true);
        dataWorker.cancel(true);
    }

    @Override
    public void destroy() {
        killWorkers();
    }
}
