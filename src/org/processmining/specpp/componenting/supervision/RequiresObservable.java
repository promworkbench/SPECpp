package org.processmining.specpp.componenting.supervision;

import org.processmining.specpp.supervision.observations.Observation;

public interface RequiresObservable<O extends Observation> {

    Class<O> getObservableClass();

}
