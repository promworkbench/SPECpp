package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EnumCounts;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.vectorization.VMHComputations;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

public class MarkingHistoryBasedFitnessEvaluator extends AbstractBasicFitnessEvaluator {

    public static final TaskDescription basic = new TaskDescription("Basic Marking Based Fitness Evaluation");
    public static final TaskDescription detailed = new TaskDescription("Detailed Marking Based Fitness Evaluation");

    private final DelegatingEvaluator<Place, VariantMarkingHistories> historyMaker = new DelegatingEvaluator<>();

    public MarkingHistoryBasedFitnessEvaluator() {
        globalComponentSystem().require(EvaluationRequirements.PLACE_MARKING_HISTORY, historyMaker);
    }

    @Override
    protected BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(basic);
        VariantMarkingHistories h = historyMaker.eval(place);
        Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator = VMHComputations.indexedFitnessComputationOn(h, consideredVariants);
        AbstractEnumSetReplayTask<ReplayUtils.ReplayOutcomes, BasicFitnessEvaluation> task = ReplayUtils.createBasicReplayTask(spliterator, getVariantFrequencies()::get);
        BasicFitnessEvaluation result = ReplayUtils.computeHere(task);
        timeStopper.stop(basic);
        return result;
    }

    @Override
    protected DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(detailed);
        VariantMarkingHistories h = historyMaker.eval(place);
        Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator = VMHComputations.indexedFitnessComputationOn(h, consideredVariants);
        AbstractEnumSetReplayTask<ReplayUtils.ReplayOutcomes, DetailedFitnessEvaluation> task = ReplayUtils.createDetailedReplayTask(spliterator, getVariantFrequencies()::get);
        DetailedFitnessEvaluation result = ReplayUtils.computeHere(task);
        timeStopper.stop(detailed);
        return result;
    }

    public static BasicFitnessEvaluation computeBasicEvaluationHere(Spliterator<IndexedItem<BasicFitnessStatus>> spliterator, IntUnaryOperator vectorFrequency) {
        int enumLength = BasicFitnessStatus.values().length;
        int[] counts = new int[enumLength];
        spliterator.forEachRemaining(rr -> counts[rr.getItem().ordinal()] += vectorFrequency.applyAsInt(rr.getIndex()));
        return BasicFitnessEvaluation.ofCounts(new EnumCounts<>(counts));
    }

    public static DetailedFitnessEvaluation computeDetailedEvaluationHere(Spliterator<IndexedItem<BasicFitnessStatus>> spliterator, IntUnaryOperator vectorFrequency) {
        int enumLength = BasicFitnessStatus.values().length;
        int[] counts = new int[enumLength];
        BitMask fitting = new BitMask();
        spliterator.forEachRemaining(rr -> {
            int i = rr.getIndex();
            BasicFitnessStatus fitnessStatus = rr.getItem();
            counts[fitnessStatus.ordinal()] += vectorFrequency.applyAsInt(i);
            if (fitnessStatus == BasicFitnessStatus.FITTING) fitting.set(i);
        });
        BasicFitnessEvaluation ev = BasicFitnessEvaluation.ofCounts(new EnumCounts<>(counts));
        return new DetailedFitnessEvaluation(fitting, ev);
    }

    @Override
    public String toString() {
        return "MarkingBasedFitnessEvaluator(" + historyMaker.getDelegate().getClass().getSimpleName() + ")";
    }


}
