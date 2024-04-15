package org.processmining.specpp.evaluation.fitness.parallelized;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.util.arraybacked.EnumCounts;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.ReplayUtils;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

public class DetailedReplayTask extends AbstractEnumSetReplayTask<ReplayOutcome, DetailedFitnessEvaluation> {
    public DetailedReplayTask(Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> toAggregate, IntUnaryOperator variantCountMapper) {
        super(toAggregate, variantCountMapper);
    }

    @Override
    protected DetailedFitnessEvaluation computeHere() {
        BitMask bm = new BitMask();
        int[] counts = new int[ReplayOutcome.values().length];
        toAggregate.forEachRemaining(ii -> {
            EnumSet<ReplayOutcome> set = ii.getItem();
            if (set.contains(ReplayOutcome.FITTING)) bm.set(ii.getIndex());
            int c = variantCountMapper.applyAsInt(ii.getIndex());
            for (ReplayOutcome e : ii.getItem()) {
                counts[e.ordinal()] += c;
            }
        });
        BasicFitnessEvaluation basicFitnessEvaluation = ReplayUtils.summarizeReplayOutcomeCounts(new EnumCounts<>(counts));
        return new DetailedFitnessEvaluation(bm, basicFitnessEvaluation);
    }

    @Override
    protected DetailedFitnessEvaluation combineIntoFirst(DetailedFitnessEvaluation first, DetailedFitnessEvaluation second) {
        first.disjointMerge(second);
        return first;
    }

    @Override
    protected AbstractEnumSetReplayTask<ReplayOutcome, DetailedFitnessEvaluation> createSubTask(Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> spliterator) {
        return new DetailedReplayTask(spliterator, variantCountMapper);
    }

}
