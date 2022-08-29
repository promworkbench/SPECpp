package org.processmining.specpp.componenting.delegators;

public interface Delegator<T> extends Container<T> {

    void setDelegate(T delegate);

    T getDelegate();

    boolean isSet();

    @Override
    default void addContent(T content) {
        setDelegate(content);
    }

    @Override
    default boolean isFull() {
        return false;
    }
    // TODO maybe, just maybe thoroughly think about this

    @Override
    default boolean isEmpty() {
        return !isSet();
    }

}
