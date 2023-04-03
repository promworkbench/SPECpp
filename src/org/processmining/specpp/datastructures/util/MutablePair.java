package org.processmining.specpp.datastructures.util;

import org.apache.commons.collections4.IteratorUtils;

import java.util.Iterator;

public class MutablePair<T> extends MutableTuple2<T, T> implements Pair<T> {
    @Override
    public T first() {
        return getT1();
    }

    @Override
    public T second() {
        return getT2();
    }

    @Override
    public Iterator<T> iterator() {
        return IteratorUtils.arrayIterator(first(), second());
    }
}
