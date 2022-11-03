package org.processmining.specpp.traits;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observable;

/**
 * Interface for a type that has an observable component.
 * E.g. a constraint generating class.
 * @param <O>
 */
public interface ExposesObservable<O extends Observation> {

    Observable<O> getObservable();

}
