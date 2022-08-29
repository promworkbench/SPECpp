package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

public interface Observations<O extends Observation> extends Iterable<O>, Observation {
}
