package org.processmining.specpp.supervision.traits;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.Observations;

public interface ToMany<O extends Observation> extends Observable<Observations<O>>, DimensionalityTrait {
}
