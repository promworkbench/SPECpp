package org.processmining.specpp.datastructures.util;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Interface for a rudimentary sequential collection aka List.
 * Used in compositions to preserve candidate acceptance order.
 * @param <T>
 */
public interface SequentialCollection<T> extends Consumer<T>, Iterable<T> {

    int size();

    boolean hasCapacityLeft();

    Set<T> toSet();

    List<T> toList();


}
