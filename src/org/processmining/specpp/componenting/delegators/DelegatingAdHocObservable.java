package org.processmining.specpp.componenting.delegators;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.AdHocObservable;
import org.processmining.specpp.supervision.piping.AsyncObserver;
import org.processmining.specpp.supervision.piping.Observer;

import java.util.Collection;
import java.util.List;

public class DelegatingAdHocObservable<O extends Observation> extends AbstractDelegator<AdHocObservable<O>> implements AdHocObservable<O> {

    public DelegatingAdHocObservable() {
    }

    public DelegatingAdHocObservable(AdHocObservable<O> delegate) {
        super(delegate);
    }

    public O computeObservation() {
        return delegate.computeObservation();
    }

    public void trigger() {
        delegate.trigger();
    }

    public void addObserver(Observer<O> observer) {
        delegate.addObserver(observer);
    }

    public Collection<Observer<O>> getObservers() {
        return delegate.getObservers();
    }

    public void removeObserver(Observer<O> observer) {
        delegate.removeObserver(observer);
    }

    public void clearObservers() {
        delegate.clearObservers();
    }

    public void publish(O observation) {
        delegate.publish(observation);
    }

    @Override
    public List<AsyncObserver<O>> getAsyncObservers() {
        return delegate.getAsyncObservers();
    }

    @Override
    public List<Observer<O>> getNonAsyncObservers() {
        return delegate.getNonAsyncObservers();
    }
}
