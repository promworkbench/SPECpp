package org.processmining.specpp.datastructures.util;

import java.util.Iterator;

public interface Pair<T> extends Tuple2<T, T>, Iterable<T> {
    T first();

    T second();

    @Override
    Iterator<T> iterator();
}
