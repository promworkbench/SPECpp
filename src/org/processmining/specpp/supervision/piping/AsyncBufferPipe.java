package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.traits.OneToMany;

import java.util.concurrent.CompletableFuture;

public class AsyncBufferPipe<O extends Observation> extends BufferPipe<O> implements AsyncObservationPipe<O, Observations<O>>, OneToMany<O, O> {

    public AsyncBufferPipe() {
        super(true);
    }

    @Override
    public void trigger() {
        publishAsync(CompletableFuture.supplyAsync(this::drainBuffer).thenApply(this::collect));
    }

    @Override
    public void observeAsync(CompletableFuture<O> futureObservation) {
        futureObservation.thenAccept(this::buffer);
    }


}
