package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

public class SelfEmptyingSummarizingBufferPipe<I extends Observation, O extends Observation> extends SummarizingBufferPipe<I, O> {
    private final int capacityThreshold;

    public SelfEmptyingSummarizingBufferPipe(ObservationSummarizer<? super I, ? extends O> summarizer, int capacityThreshold) {
        super(summarizer);
        this.capacityThreshold = capacityThreshold;
    }

    @Override
    protected void buffer(I observation) {
        super.buffer(observation);
        if (buffer.size() >= capacityThreshold) flushBuffer();
    }
}
