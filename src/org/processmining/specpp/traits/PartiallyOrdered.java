package org.processmining.specpp.traits;

public interface PartiallyOrdered<T> {

    boolean gt(T other);

    boolean lt(T other);

    default boolean equivalent(T other) {
        return lt(other) && gt(other);
    }

}
