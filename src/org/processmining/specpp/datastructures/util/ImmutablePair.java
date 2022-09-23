package org.processmining.specpp.datastructures.util;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.function.Function;

public class ImmutablePair<T> extends ImmutableTuple2<T, T> implements Pair<T> {


    public ImmutablePair(T t1, T t2) {
        super(t1, t2);
    }

    @Override
    public T first() {
        return t1;
    }

    @Override
    public T second() {
        return t2;
    }

    @Override
    public Iterator<T> iterator() {
        return ImmutableList.of(t1, t2).iterator();
    }


    public static <T, R> Pair<R> map(Pair<T> pair, Function<T, R> func) {
        return new ImmutablePair<>(func.apply(pair.first()), func.apply(pair.second()));
    }

}
