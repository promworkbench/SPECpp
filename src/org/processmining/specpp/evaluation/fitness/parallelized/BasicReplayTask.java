package org.processmining.specpp.evaluation.fitness.parallelized;

import org.processmining.specpp.datastructures.util.arraybacked.EnumCounts;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.ReplayUtils;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

public class BasicReplayTask extends AbstractEnumSetReplayTask<ReplayOutcome, BasicFitnessEvaluation> {

    public BasicReplayTask(Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> toAggregate, IntUnaryOperator variantCountMapper) {
        super(toAggregate, variantCountMapper);
    }

    @Override
    protected BasicFitnessEvaluation computeHere() {
        int[] counts = new int[ReplayOutcome.values().length];
        toAggregate.forEachRemaining(ii -> {
            int c = variantCountMapper.applyAsInt(ii.getIndex());
            for (ReplayOutcome e : ii.getItem()) {
                counts[e.ordinal()] += c;
            }
        });
        return ReplayUtils.summarizeReplayOutcomeCounts(new EnumCounts<>(counts));
    }

    @Override
    protected BasicFitnessEvaluation combineIntoFirst(BasicFitnessEvaluation first, BasicFitnessEvaluation second) {
        first.disjointMerge(second);
        return first;
    }

    @Override
    protected BasicReplayTask createSubTask(Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> spliterator) {
        return new BasicReplayTask(spliterator, variantCountMapper);
    }

}
