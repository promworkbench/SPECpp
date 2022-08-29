package org.processmining.specpp.supervision.traits;

import org.processmining.specpp.supervision.observations.Observation;

public interface ManyToMany<I extends Observation, O extends Observation> extends FromMany<I>, ToMany<O> {
}
