package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.traits.OneToOne;

public class IdentityPipe<O extends Observation> extends AbstractAsyncAwareObservable<O> implements TypeIdentPipe<O>, OneToOne<O, O> {
    @Override
    public void observe(O observation) {
        publish(observation);
    }

}
