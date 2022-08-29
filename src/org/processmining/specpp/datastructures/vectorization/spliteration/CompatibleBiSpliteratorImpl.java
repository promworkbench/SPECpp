package org.processmining.specpp.datastructures.vectorization.spliteration;

import org.processmining.specpp.datastructures.util.MutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class CompatibleBiSpliteratorImpl<T, U, V> extends BiSpliteratorImpl<T, U> implements Spliterator<V> {

    protected final Function<? super Tuple2<T, U>, V> combiner;

    public CompatibleBiSpliteratorImpl(Spliterator<T> firstSpliterator, Spliterator<U> secondSpliterator, Function<? super Tuple2<T, U>, V> combiner) {
        super(firstSpliterator, secondSpliterator);
        this.combiner = combiner;
    }

    protected MutableTuple2<T, U> createContainer() {
        return new MutableTuple2<>();
    }

    @Override
    public boolean tryAdvance(Consumer<? super V> action) {
        MutableTuple2<T, U> res = createContainer();
        boolean a = firstSpliterator.tryAdvance(res::setT1);
        boolean b = secondSpliterator.tryAdvance(res::setT2);
        assert a == b;
        if (a && b) {
            V v = combiner.apply(res);
            action.accept(v);
            return true;
        } else return false;
    }

    @Override
    public void forEachRemaining(Consumer<? super V> action) {
        Spliterator.super.forEachRemaining(action);
    }

    @Override
    public long getExactSizeIfKnown() {
        return Spliterator.super.getExactSizeIfKnown();
    }

    @Override
    public boolean hasCharacteristics(int characteristics) {
        return Spliterator.super.hasCharacteristics(characteristics);
    }

    @Override
    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override
    public CompatibleBiSpliteratorImpl<T, U, V> trySplit() {
        Spliterator<T> splitA = firstSpliterator.trySplit();
        Spliterator<U> splitB = secondSpliterator.trySplit();
        assert firstSpliterator.estimateSize() == secondSpliterator.estimateSize();
        return new CompatibleBiSpliteratorImpl<>(splitA, splitB, combiner);
    }

}
