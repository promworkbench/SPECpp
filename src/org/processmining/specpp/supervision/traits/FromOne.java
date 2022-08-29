package org.processmining.specpp.supervision.traits;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observer;

public interface FromOne<I extends Observation> extends Observer<I>, DimensionalityTrait {
}
