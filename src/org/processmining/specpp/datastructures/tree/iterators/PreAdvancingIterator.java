package org.processmining.specpp.datastructures.tree.iterators;

import java.util.Iterator;

public abstract class PreAdvancingIterator<T> implements Iterator<T> {

    protected T current;

    protected PreAdvancingIterator(T initial) {
        this.current = initial;
        advance();
    }

    protected PreAdvancingIterator() {
        this.current = null;
    }


    protected abstract T advance();

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public T next() {
        T temp = current;
        if (current != null) current = advance();
        return temp;
    }
}
