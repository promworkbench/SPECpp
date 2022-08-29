package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

public class CountingBufferPipe<O extends Observation> extends BufferPipe<O> {

    private final int threshold;

    public CountingBufferPipe(int threshold) {
        assert threshold > 0;
        this.threshold = threshold;
    }

    @Override
    protected void buffer(O observation) {
        super.buffer(observation);
        if (buffer.size() >= threshold) trigger();
    }
}
