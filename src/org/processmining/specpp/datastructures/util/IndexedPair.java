package org.processmining.specpp.datastructures.util;

public class IndexedPair<T> extends IndexedTuple2<T, T> {
    public IndexedPair() {
    }

    public IndexedPair(int index, T t, T t2) {
        super(index, t, t2);
    }
}
