package org.processmining.specpp.datastructures.util;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;

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
}
