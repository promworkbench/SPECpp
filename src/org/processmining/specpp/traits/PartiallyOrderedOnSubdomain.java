package org.processmining.specpp.traits;

public interface PartiallyOrderedOnSubdomain<D, T> extends PartiallyOrdered<T> {

    boolean gtOn(D domain, T other);

    boolean ltOn(D domain, T other);

    default boolean equivalentOn(D domain, T other) {
        return ltOn(domain, other) && gtOn(domain, other);
    }


}
