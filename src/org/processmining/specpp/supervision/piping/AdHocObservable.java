package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.traits.Triggerable;

public interface AdHocObservable<O extends Observation> extends AsyncAwareObservable<O>, Triggerable {

    O computeObservation();

    @Override
    default void trigger() {
        publish(computeObservation());
    }

}
