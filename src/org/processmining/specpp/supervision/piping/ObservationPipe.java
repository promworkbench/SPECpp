package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

public interface ObservationPipe<I extends Observation, O extends Observation> extends Observer<I>, Observable<O> {

}
