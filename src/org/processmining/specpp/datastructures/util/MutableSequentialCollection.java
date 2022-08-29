package org.processmining.specpp.datastructures.util;

public interface MutableSequentialCollection<T> extends SequentialCollection<T> {

    void remove(T item);

    T removeLast();

}
