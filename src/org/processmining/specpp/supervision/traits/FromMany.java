package org.processmining.specpp.supervision.traits;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observations;
import org.processmining.specpp.supervision.piping.Observer;

public interface FromMany<O extends Observation> extends Observer<Observations<O>>, DimensionalityTrait {
}
