package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.componenting.delegators.AbstractDelegator;
import org.processmining.specpp.supervision.observations.Observation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;

public class ObservationCollection<O extends Observation> extends AbstractDelegator<Collection<O>> implements Collection<O>, Observations<O> {

    public ObservationCollection(Collection<O> internal) {
        super(internal);
    }

    @Override

    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public Spliterator<O> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<O> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(O o) {
        return delegate.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends O> c) {
        return delegate.addAll(c);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
