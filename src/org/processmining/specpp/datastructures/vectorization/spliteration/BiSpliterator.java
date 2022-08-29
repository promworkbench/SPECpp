package org.processmining.specpp.datastructures.vectorization.spliteration;

import java.util.Comparator;
import java.util.function.BiConsumer;

import static java.util.Spliterator.SIZED;

public interface BiSpliterator<T, U> {
    boolean tryAdvance(BiConsumer<? super T, ? super U> action);


    default void forEachRemaining(BiConsumer<? super T, ? super U> action) {
        do {
        } while (tryAdvance(action));
    }

    BiSpliterator<T, U> trySplit();

    long estimateSize();

    default long getExactSizeIfKnown() {
        return (characteristics() & SIZED) == 0 ? -1L : estimateSize();
    }

    int characteristics();

    default boolean hasCharacteristics(int characteristics) {
        return (characteristics() & characteristics) == characteristics;
    }

    default Comparator<? super T> getComparator() {
        throw new IllegalStateException();
    }

    interface OfPrimitive<T, T_CONS, T_SPLITR extends BiSpliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends BiSpliterator<T, T> {
        @Override
        T_SPLITR trySplit();

        @SuppressWarnings("overloads")
        boolean tryAdvance(T_CONS action);

        @SuppressWarnings("overloads")
        default void forEachRemaining(T_CONS action) {
            do {
            } while (tryAdvance(action));
        }
    }

    interface OfInt extends BiSpliterator.OfPrimitive<Integer, IntBiConsumer, BiSpliterator.OfInt> {

        @Override
        OfInt trySplit();

        @Override
        boolean tryAdvance(IntBiConsumer action);

        @Override
        default void forEachRemaining(IntBiConsumer action) {
            do {
            } while (tryAdvance(action));
        }


        @Override
        default boolean tryAdvance(BiConsumer<? super Integer, ? super Integer> action) {
            if (action instanceof IntBiConsumer) {
                return tryAdvance((IntBiConsumer) action);
            } else {
                return tryAdvance((IntBiConsumer) action::accept);
            }
        }

        @Override
        default void forEachRemaining(BiConsumer<? super Integer, ? super Integer> action) {
            if (action instanceof IntBiConsumer) {
                forEachRemaining((IntBiConsumer) action);
            } else {
                forEachRemaining((IntBiConsumer) action::accept);
            }
        }
    }

}
