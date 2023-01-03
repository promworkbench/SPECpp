package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingObservable;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.specpp.datastructures.tree.constraints.CullPostsetChildren;
import org.processmining.specpp.datastructures.tree.constraints.CullPresetChildren;
import org.processmining.specpp.datastructures.tree.events.*;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.supervision.monitoring.AccumulatingToIntWrapper;
import org.processmining.specpp.supervision.monitoring.ComputingMonitor;
import org.processmining.specpp.supervision.observations.Event;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.traits.RepresentsChange;
import org.processmining.specpp.util.JavaTypingUtils;

import java.math.BigInteger;
import java.util.IntSummaryStatistics;

public class EvaluationSupervisor extends MonitoringSupervisor {

    private final DelegatingObservable<TreeHeuristicsEvent> heuristicsEvents = new DelegatingObservable<>();
    private final DelegatingObservable<TreeEvent> treeEvents = new DelegatingObservable<>();
    private final DelegatingObservable<GenerationConstraint> generationConstraint = new DelegatingObservable<>();
    private final DelegatingDataSource<IntEncodings<Transition>> encodedTransitions = new DelegatingDataSource<>();

    public EvaluationSupervisor() {
        globalComponentSystem().require(DataRequirements.ENC_TRANS, encodedTransitions)
                               .require(SupervisionRequirements.observable("heuristics.events", JavaTypingUtils.castClass(HeuristicComputationEvent.class)), heuristicsEvents)
                               .require(SupervisionRequirements.observable("tree.events", TreeEvent.class), treeEvents)
                               .require(SupervisionRequirements.observable("proposer.constraints", GenerationConstraint.class), generationConstraint);

    }

    public static class PruningMonitor implements ComputingMonitor<GenerationConstraint, BigInteger, String> {

        private final BigInteger totalTreeNodes;
        private BigInteger accumulator;

        public PruningMonitor(BigInteger totalTreeNodes) {
            this.totalTreeNodes = totalTreeNodes;
            accumulator = BigInteger.ZERO;
        }

        @Override
        public BigInteger getMonitoringState() {
            return accumulator;
        }

        @Override
        public void handleObservation(GenerationConstraint gc) {
            if (gc instanceof CullPostsetChildren) {
                CullPostsetChildren cullPostsetChildren = (CullPostsetChildren) gc;
                int i = cullPostsetChildren.getAffectedNode().getState().getPotentialPostsetExpansions().cardinality();
                BigInteger cutoffNodes = BigInteger.valueOf(2).pow(i).subtract(BigInteger.ONE);
                accumulator = accumulator.add(cutoffNodes);
            } else if (gc instanceof CullPresetChildren) {
                CullPresetChildren cullPresetChildren = (CullPresetChildren) gc;
                int i = cullPresetChildren.getAffectedNode().getState().getPotentialPresetExpansions().cardinality();
                BigInteger cutoffNodes = BigInteger.valueOf(2).pow(i).subtract(BigInteger.ONE);
                accumulator = accumulator.add(cutoffNodes);
            }
        }

        @Override
        public String computeResult() {
            double doubleTotalTreeNodes = totalTreeNodes.doubleValue();
            double doublePrunedTreeNodes = accumulator.doubleValue();
            return String.format("%s/%s (%s%%) tree nodes were pruned by sub tree cutoff constraints.", accumulator, totalTreeNodes, doublePrunedTreeNodes / doubleTotalTreeNodes);
        }
    }

    public static class AccumulatingIntChangeMonitor<E extends Event & RepresentsChange> implements ComputingMonitor<E, IntSummaryStatistics, String> {

        private final IntSummaryStatistics iss;
        private final AccumulatingToIntWrapper<RepresentsChange> accumulator;

        public AccumulatingIntChangeMonitor() {
            iss = new IntSummaryStatistics();
            accumulator = new AccumulatingToIntWrapper<>(RepresentsChange::getDelta);
        }

        @Override
        public IntSummaryStatistics getMonitoringState() {
            return iss;
        }

        @Override
        public void handleObservation(E observation) {
            iss.accept(accumulator.applyAsInt(observation));
        }

        @Override
        public String computeResult() {
            return iss.toString();
        }
    }

    @Override
    protected void instantiateObservationHandlingFullySatisfied() {
        IntEncodings<Transition> enc = encodedTransitions.getData();
        int Apre = enc.pre().size();
        int Apost = enc.post().size();
        BigInteger totalTreeNodes = BigInteger.valueOf(2)
                                              .pow(Apre + Apost - 2)
                                              .add(BigInteger.ONE)
                                              .add(BigInteger.valueOf(Apost));

        createMonitor("pruning", new PruningMonitor(totalTreeNodes));
        createMonitor("heuristics.queue", new AccumulatingIntChangeMonitor<TreeHeuristicQueueingEvent<PlaceNode>>());
        createMonitor("tree.leaves", new AccumulatingIntChangeMonitor<LeafEvent<PlaceNode>>());

        beginLaying().source(generationConstraint).terminalSink(getMonitor("pruning"));

        beginLaying().source(treeEvents)
                     .pipe(PipeWorks.predicatePipe(e -> e instanceof LeafEvent))
                     .terminalSink(getMonitor("tree.leaves"));

        beginLaying().source(heuristicsEvents)
                     .pipe(PipeWorks.predicatePipe(e -> e instanceof TreeHeuristicQueueingEvent))
                     .terminalSink(getMonitor("heuristics.queue"));
    }


}
