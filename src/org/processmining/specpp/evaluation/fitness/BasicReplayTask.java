package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.datastructures.util.EnumCounts;
import org.processmining.specpp.datastructures.util.IndexedItem;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

public class BasicReplayTask extends AbstractEnumSetReplayTask<ReplayUtils.ReplayOutcomes, BasicFitnessEvaluation> {

    public BasicReplayTask(Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> toAggregate, IntUnaryOperator variantCountMapper, int enumLength) {
        super(toAggregate, variantCountMapper, enumLength);
    }

    @Override
    protected BasicFitnessEvaluation computeHere() {
        int[] counts = new int[enumLength];
        toAggregate.forEachRemaining(ii -> {
            int c = variantCountMapper.applyAsInt(ii.getIndex());
            for (ReplayUtils.ReplayOutcomes e : ii.getItem()) {
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
    protected BasicReplayTask createSubTask(int enumLength, Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator) {
        return new BasicReplayTask(spliterator, variantCountMapper, enumLength);
    }

}
