package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.concurrent.CompletableFuture;

public class AsyncSummarizingBufferPipe<I extends Observation, O extends Observation> extends SummarizingBufferPipe<I, O> implements AsyncObservationPipe<I, O> {

    public AsyncSummarizingBufferPipe(ObservationSummarizer<I, O> collator) {
        super(collator, true);
    }

    @Override
    public void observeAsync(CompletableFuture<I> futureObservation) {
        futureObservation.thenAccept(this::buffer);
    }

    @Override
    public void trigger() {
        publishAsync(CompletableFuture.supplyAsync(this::drainBuffer).thenApply(this::collect));
    }

}
