package org.processmining.specpp.evaluation.fitness.parallelized;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.util.arraybacked.EnumCounts;
import org.processmining.specpp.datastructures.util.arraybacked.EnumMapping;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;
import org.processmining.specpp.evaluation.fitness.ReplayUtils;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

public class ComprehensiveReplayTask extends AbstractEnumSetReplayTask<ReplayOutcome, ComprehensiveFitnessEvaluation> {
    public ComprehensiveReplayTask(Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> toAggregate, IntUnaryOperator variantCountMapper) {
        super(toAggregate, variantCountMapper);
    }


    @Override
    protected ComprehensiveFitnessEvaluation computeHere() {
        BitMask[] bms = ReplayUtils.createBitMaskArray();

        BitMask bm = new BitMask();
        int[] counts = new int[ReplayOutcome.values().length];

        toAggregate.forEachRemaining(ii -> {
            for (ReplayOutcome e : ii.getItem()) {
                bms[e.ordinal()].set(ii.getIndex());
            }
        });

        EnumMapping<ReplayOutcome, BitMask> bmMapping = new EnumMapping<>(bms);
        EnumCounts<ReplayOutcome> enumCounts = ReplayUtils.aggregateReplayOutcomeBitMasks(bmMapping, variantCountMapper);
        BasicFitnessEvaluation basicFitnessEvaluation = ReplayUtils.summarizeReplayOutcomeCounts(enumCounts);
        return new ComprehensiveFitnessEvaluation(bmMapping, basicFitnessEvaluation);
    }

    @Override
    protected ComprehensiveFitnessEvaluation combineIntoFirst(ComprehensiveFitnessEvaluation first, ComprehensiveFitnessEvaluation second) {
        first.disjointMerge(second);
        return first;
    }

    @Override
    protected AbstractEnumSetReplayTask<ReplayOutcome, ComprehensiveFitnessEvaluation> createSubTask(Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> spliterator) {
        return new ComprehensiveReplayTask(spliterator, variantCountMapper);
    }

}
