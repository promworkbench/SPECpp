package org.processmining.specpp.datastructures.encoding;

public interface PrimitiveIntSet extends SlightlyMutableSet<Integer> {

    boolean containsInt(int item);

    @Override
    default boolean contains(Integer item) {
        return containsInt(item);
    }

    boolean containsIndex(int index);

    boolean addInt(int item);

    @Override
    default boolean add(Integer item) {
        return addInt(item);
    }

    boolean removeInt(int item);

    @Override
    default boolean remove(Integer item) {
        return removeInt(item);
    }
}
