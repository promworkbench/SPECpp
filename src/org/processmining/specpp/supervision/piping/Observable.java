package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.Collection;

public interface Observable<O extends Observation> {

    void addObserver(Observer<O> observer);

    Collection<Observer<O>> getObservers();

    void removeObserver(Observer<O> observer);

    void clearObservers();

    void publish(O observation);

    default void beObservedBy(Observer<O> observer) {
        addObserver(observer);
    }


}
