package org.processmining.specpp.componenting.supervision;

import org.processmining.specpp.supervision.observations.Observation;

public interface RequiresObserver<O extends Observation> {

    Class<O> getObservedClass();

}
