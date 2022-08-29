package org.processmining.specpp.componenting.delegators;

public abstract class AbstractDelegator<T> implements Delegator<T> {

    protected T delegate;

    public AbstractDelegator() {
    }

    public AbstractDelegator(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setDelegate(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isSet() {
        return delegate != null;
    }

    public T getDelegate() {
        return delegate;
    }

    @Override
    public String toString() {
        return delegate == null ? super.toString() : delegate.toString();
    }

    @Override
    public int hashCode() {
        return delegate == null ? super.hashCode() : delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate == null ? super.equals(obj) : delegate.equals(obj);
    }

}
