package org.processmining.specpp.datastructures.util;

/**
 * Interface for a sequential collection that is also mutable.
 *
 * @param <T>
 */
public interface MutableSequentialCollection<T> extends SequentialCollection<T> {

    void remove(T item);

    T removeLast();

}
