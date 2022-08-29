package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractObservable<O extends Observation> implements Observable<O> {

    private final List<Observer<O>> observers;

    public AbstractObservable() {
        observers = new LinkedList<>();
    }

    @Override
    public void addObserver(Observer<O> observer) {
        if (!observers.contains(observer))
            observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<O> observer) {
        observers.remove(observer);
    }

    @Override
    public void clearObservers() {
        observers.clear();
    }

    @Override
    public Collection<Observer<O>> getObservers() {
        return observers;
    }

    @Override
    public void publish(O observation) {
        if (observation != null) {
            for (Observer<O> observer : getObservers()) {
                observer.observe(observation);
            }
        }
    }

}
