package org.processmining.specpp.datastructures.vectorization.spliteration;

import org.processmining.specpp.datastructures.util.MutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.datastructures.util.MutablePair;

import java.util.Spliterator;
import java.util.function.Function;

public class CompatiblePairSpliteratorImpl<T, V> extends CompatibleBiSpliteratorImpl<T, T, V> {
    public CompatiblePairSpliteratorImpl(Spliterator<T> firstSpliterator, Spliterator<T> secondSpliterator, Function<? super Tuple2<T, T>, V> combiner) {
        super(firstSpliterator, secondSpliterator, combiner);
    }

    @Override
    protected MutableTuple2<T, T> createContainer() {
        return new MutablePair<>();
    }

}
