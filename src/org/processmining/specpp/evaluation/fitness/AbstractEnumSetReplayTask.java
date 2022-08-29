package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.datastructures.util.IndexedItem;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.concurrent.RecursiveTask;
import java.util.function.IntUnaryOperator;

public abstract class AbstractEnumSetReplayTask<E extends Enum<E>, R> extends RecursiveTask<R> {
    protected static final long MIN_SPLITTING_SIZE = 100;
    protected final Spliterator<IndexedItem<EnumSet<E>>> toAggregate;
    protected final IntUnaryOperator variantCountMapper;
    protected final int enumLength;

    public AbstractEnumSetReplayTask(Spliterator<IndexedItem<EnumSet<E>>> toAggregate, IntUnaryOperator variantCountMapper, int enumLength) {
        this.toAggregate = toAggregate;
        this.variantCountMapper = variantCountMapper;
        this.enumLength = enumLength;
    }

    protected abstract R computeHere();

    protected abstract R combineIntoFirst(R first, R second);

    protected abstract AbstractEnumSetReplayTask<E, R> createSubTask(int enumLength, Spliterator<IndexedItem<EnumSet<E>>> spliterator);

    @Override
    protected R compute() {
        if (toAggregate.getExactSizeIfKnown() < MIN_SPLITTING_SIZE) {
            return computeHere();
        } else {
            Spliterator<IndexedItem<EnumSet<E>>> split = toAggregate.trySplit();
            if (split == null) return computeHere();
            else {
                AbstractEnumSetReplayTask<E, R> taskA = createSubTask(enumLength, toAggregate);
                AbstractEnumSetReplayTask<E, R> taskB = createSubTask(enumLength, split);
                taskB.fork();
                return combineIntoFirst(taskA.compute(), taskB.join());
            }
        }
    }
}
