package org.processmining.specpp.datastructures.vectorization.spliteration;

import java.util.Spliterator;

public class PairSpliteratorImpl<T> extends BiSpliteratorImpl<T, T> implements PairSpliterator<T> {
    public PairSpliteratorImpl(Spliterator<T> firstSpliterator, Spliterator<T> secondSpliterator) {
        super(firstSpliterator, secondSpliterator);
    }
}
