package org.processmining.specpp.datastructures.encoding;

/**
 * Specifies mutating set operations on sets.
 * The static definitions allow non mutating application of these methods on copyable implementations.
 *
 * @param <T>
 */
public interface MutatingSetOperations<T extends MutatingSetOperations<T>> {


    void union(T other);

    void setminus(T other);

    void intersection(T other);

}
