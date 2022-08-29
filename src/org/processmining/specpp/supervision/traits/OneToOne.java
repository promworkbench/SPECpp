package org.processmining.specpp.supervision.traits;

import org.processmining.specpp.supervision.observations.Observation;

public interface OneToOne<O extends Observation, C extends Observation> extends FromOne<O>, ToOne<C> {
}
