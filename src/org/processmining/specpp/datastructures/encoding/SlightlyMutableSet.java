package org.processmining.specpp.datastructures.encoding;

public interface SlightlyMutableSet<T> {

    boolean contains(T item);

    boolean add(T item);

    boolean remove(T item);

    void addAll(T... items);

    default int size() {
        return cardinality();
    }

    int cardinality();

    boolean isEmpty();

    void clear();

}
