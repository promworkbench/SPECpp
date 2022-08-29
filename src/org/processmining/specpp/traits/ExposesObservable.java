package org.processmining.specpp.traits;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observable;

public interface ExposesObservable<O extends Observation> {

    Observable<O> getObservable();

}
