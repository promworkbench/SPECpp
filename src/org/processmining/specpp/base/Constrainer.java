package org.processmining.specpp.base;

import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.traits.ExposesObservable;

/**
 * A type that exposes an observable that specifically publishes {@code ConstraintEvent}s of type {@code L}.
 *
 * @param <L> type of constraint events
 */
public interface Constrainer<L extends ConstraintEvent> extends ExposesObservable<L> {

    Observable<L> getConstraintPublisher();

    @Override
    default Observable<L> getObservable() {
        return getConstraintPublisher();
    }

    Class<L> getPublishedConstraintClass();

}
