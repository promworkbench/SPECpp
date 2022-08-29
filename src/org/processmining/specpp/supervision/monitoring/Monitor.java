package org.processmining.specpp.supervision.monitoring;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observer;

public interface Monitor<O extends Observation, R> extends Observer<O> {

    R getMonitoringState();

    void handleObservation(O observation);

    @Override
    default void observe(O observation) {
        handleObservation(observation);
    }

}
