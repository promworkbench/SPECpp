package org.processmining.specpp.supervision.traits;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observable;

public interface ToOne<O extends Observation> extends Observable<O>, DimensionalityTrait {
}
