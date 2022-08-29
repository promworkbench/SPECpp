package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

public class SummarizingBufferPipe<I extends Observation, O extends Observation> extends AbstractBufferingPipe<I, O> {

    private final ObservationSummarizer<? super I, ? extends O> collator;

    public SummarizingBufferPipe(ObservationSummarizer<? super I, ? extends O> summarizer) {
        this(summarizer, false);
    }

    protected SummarizingBufferPipe(ObservationSummarizer<? super I, ? extends O> summarizer, boolean useConcurrentBuffer) {
        super(useConcurrentBuffer);
        this.collator = summarizer;
    }

    @Override
    protected O collect(Observations<I> bufferedObservations) {
        return collator.summarize(bufferedObservations);
    }

}
