package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

public class SkippingPipe<O extends Observation> extends IdentityPipe<O> {

    private final int interval;
    private int count;

    public SkippingPipe(int interval) {
        assert interval > 0;
        this.interval = interval;
    }

    @Override
    public void observe(O observation) {
        if (++count >= interval) {
            publish(observation);
            count = 0;
        }
    }
}
