package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.concurrent.CompletableFuture;

public class AsyncIdentityPipe<O extends Observation> extends IdentityPipe<O> implements AsyncObservationPipe<O, O> {

    @Override
    public void observe(O observation) {
        publishAsync(CompletableFuture.supplyAsync(() -> observation));
    }

    @Override
    public void observeAsync(CompletableFuture<O> futureObservation) {
        publishAsync(futureObservation);
    }

}
