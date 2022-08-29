package org.processmining.specpp.datastructures.vectorization.spliteration;

import java.util.Spliterator;
import java.util.function.BiConsumer;

public class BiSpliteratorImpl<T, U> implements BiSpliterator<T, U> {

    protected final Spliterator<T> firstSpliterator;
    protected final Spliterator<U> secondSpliterator;

    public BiSpliteratorImpl(Spliterator<T> firstSpliterator, Spliterator<U> secondSpliterator) {
        assert firstSpliterator.estimateSize() == secondSpliterator.estimateSize();
        assert firstSpliterator.hasCharacteristics(Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED);
        assert secondSpliterator.hasCharacteristics(Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED);
        this.firstSpliterator = firstSpliterator;
        this.secondSpliterator = secondSpliterator;
    }


    @Override
    public boolean tryAdvance(BiConsumer<? super T, ? super U> action) {
        Object[] temp = new Object[2];
        boolean a = firstSpliterator.tryAdvance(t -> temp[0] = t);
        boolean b = secondSpliterator.tryAdvance(u -> temp[1] = u);
        assert a == b;
        if (a && b) {
            action.accept((T) temp[0], (U) temp[1]);
            return true;
        } else return false;
    }


    @Override
    public void forEachRemaining(BiConsumer<? super T, ? super U> action) {
        BiSpliterator.super.forEachRemaining(action);
    }

    @Override
    public BiSpliteratorImpl<T, U> trySplit() {
        Spliterator<T> splitA = firstSpliterator.trySplit();
        Spliterator<U> splitB = secondSpliterator.trySplit();
        assert firstSpliterator.estimateSize() == secondSpliterator.estimateSize();
        return new BiSpliteratorImpl<>(splitA, splitB);
    }

    @Override
    public long estimateSize() {
        long a = firstSpliterator.estimateSize();
        long b = secondSpliterator.estimateSize();
        assert a == b;
        return a;
    }

    @Override
    public int characteristics() {
        return firstSpliterator.characteristics() & secondSpliterator.characteristics();
    }

    public static class OfInt implements BiSpliterator.OfInt {

        private final Spliterator.OfInt firstSpliterator;
        private final Spliterator.OfInt secondSpliterator;

        public OfInt(Spliterator.OfInt firstSpliterator, Spliterator.OfInt secondSpliterator) {
            assert firstSpliterator.estimateSize() == secondSpliterator.estimateSize();
            assert firstSpliterator.hasCharacteristics(Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED);
            assert secondSpliterator.hasCharacteristics(Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED);
            this.firstSpliterator = firstSpliterator;
            this.secondSpliterator = secondSpliterator;
        }

        @Override
        public boolean tryAdvance(IntBiConsumer action) {
            // sadly using boxing here
            Integer[] temp = new Integer[2];
            boolean a = firstSpliterator.tryAdvance((int t) -> temp[0] = t);
            boolean b = secondSpliterator.tryAdvance((int u) -> temp[1] = u);
            assert a == b;
            if (a && b) {
                action.accept(temp[0], temp[1]);
                return true;
            } else return false;
        }

        @Override
        public BiSpliterator.OfInt trySplit() {
            Spliterator.OfInt splitA = firstSpliterator.trySplit();
            Spliterator.OfInt splitB = secondSpliterator.trySplit();
            assert firstSpliterator.estimateSize() == secondSpliterator.estimateSize();
            return new BiSpliteratorImpl.OfInt(splitA, splitB);
        }

        @Override
        public long estimateSize() {
            long a = firstSpliterator.estimateSize();
            long b = secondSpliterator.estimateSize();
            assert a == b;
            return a;
        }

        @Override
        public int characteristics() {
            return firstSpliterator.characteristics() & secondSpliterator.characteristics();
        }
    }


}
