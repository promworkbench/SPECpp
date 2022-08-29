package org.processmining.specpp.supervision.piping;

import java.util.Collection;

public interface Buffer<E> {

    void store(E element);

    void storeAll(Collection<E> elements);

    Collection<E> drain();

    int size();

    boolean isEmpty();
}
