package org.processmining.specpp.datastructures.vectorization.spliteration;

import org.processmining.specpp.datastructures.vectorization.IntVectorStorage;

import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class AbstractSplitty<T> implements Spliterator<T> {

    protected final IntVectorStorage ivs;
    protected int startVectorIndex, fenceVectorIndex;

    public AbstractSplitty(IntVectorStorage ivs, int startVectorIndex, int fenceVectorIndex) {
        this.ivs = ivs;
        this.startVectorIndex = startVectorIndex;
        this.fenceVectorIndex = fenceVectorIndex;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (startVectorIndex < fenceVectorIndex) {
            action.accept(make(startVectorIndex++));
            return true;
        } else return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        if ((fenceVectorIndex - startVectorIndex) < IntVectorStorage.MIN_SPLIT_VECTOR_COUNT) return null;
        int low = startVectorIndex;
        int mid = low + fenceVectorIndex >>> 1;
        startVectorIndex = mid;
        return makePrefix(low, mid);
    }

    protected abstract T make(int index);

    protected abstract AbstractSplitty<T> makePrefix(int low, int mid);

    @Override
    public long estimateSize() {
        return fenceVectorIndex - startVectorIndex;
    }

    @Override
    public int characteristics() {
        return Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.CONCURRENT;
    }

}
