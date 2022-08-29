package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.function.Function;

@FunctionalInterface
public interface ObservationTransformer<I extends Observation, O extends Observation> extends Function<I, O> {

}
