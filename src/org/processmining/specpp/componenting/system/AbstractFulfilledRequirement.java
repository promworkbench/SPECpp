package org.processmining.specpp.componenting.system;

import org.processmining.specpp.traits.PartiallyOrdered;

public abstract class AbstractFulfilledRequirement<D, R extends PartiallyOrdered<R>> implements FulfilledRequirement<D, R> {

    protected final R requirement;
    protected final Class<D> delegateClass;
    protected final D delegate;

    public AbstractFulfilledRequirement(R requirement, Class<D> delegateClass, D delegate) {
        this.requirement = requirement;
        this.delegateClass = delegateClass;
        this.delegate = delegate;
    }

    @Override
    public D getContent() {
        return delegate;
    }

    @Override
    public R getComparable() {
        return requirement;
    }

    @Override
    public boolean gt(R other) {
        return getComparable().gt(other);
    }

    @Override
    public boolean lt(R other) {
        return getComparable().lt(other);
    }

    @Override
    public Class<D> contentClass() {
        return delegateClass;
    }

    @Override
    public String toString() {
        return "Fulfilled(" + requirement.toString() + " with " + getContent() + ")";
    }

}
