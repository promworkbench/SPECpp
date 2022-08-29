package org.processmining.specpp.datastructures.vectorization.spliteration;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.vectorization.IntVectorStorage;

import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class AbstractBitMaskSplitty<T> implements Spliterator<T> {

    protected final IntVectorStorage ivs;
    protected final BitMask bitMask;
    protected int prevIndex, nextIndex, count, limit;

    public AbstractBitMaskSplitty(IntVectorStorage ivs, BitMask bitMask, int startIndex, int limit) {
        this.ivs = ivs;
        this.bitMask = bitMask;
        this.limit = limit;
        prevIndex = startIndex;
        nextIndex = bitMask.nextSetBit(startIndex);
        count = 0;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (count < limit && nextIndex >= 0) {
            prevIndex = nextIndex;
            nextIndex = bitMask.nextSetBit(nextIndex + 1);
            ++count;
            action.accept(make(prevIndex));
            return true;
        } else return false;
    }

    protected abstract T make(int index);

    @Override
    public Spliterator<T> trySplit() {
        int rem = limit - count;
        if (rem < IntVectorStorage.MIN_SPLIT_VECTOR_COUNT) return null;
        int half = rem >>> 1;
        int oldStart = prevIndex;
        nextIndex = forward(bitMask, nextIndex, half + 1);
        limit -= half;
        count = 0;
        return makePrefix(oldStart, half);
    }

    public static int forward(BitMask bm, int start, int range) {
        int i, count = 0;
        i = bm.nextSetBit(start);
        while (++count < range && i >= 0) {
            i = bm.nextSetBit(i + 1);
        }
        return i;
    }

    protected abstract AbstractBitMaskSplitty<T> makePrefix(int oldStart, int half);


    @Override
    public long estimateSize() {
        return limit - count;
    }

    @Override
    public int characteristics() {
        return Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.SUBSIZED | Spliterator.SIZED | Spliterator.IMMUTABLE | Spliterator.CONCURRENT;
    }

}
