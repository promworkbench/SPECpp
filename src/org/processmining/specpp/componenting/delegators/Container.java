package org.processmining.specpp.componenting.delegators;

public interface Container<T> {

    void addContent(T content);

    boolean isEmpty();

    boolean isFull();

    default boolean isNonEmpty() {
        return !isEmpty();
    }

}
