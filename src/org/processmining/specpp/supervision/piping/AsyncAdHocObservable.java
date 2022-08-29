package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.concurrent.CompletableFuture;

public interface AsyncAdHocObservable<O extends Observation> extends AsyncObservable<O>, AdHocObservable<O> {

    default CompletableFuture<O> computeObservationAsync() {
        return CompletableFuture.supplyAsync(this::computeObservation);
    }

    @Override
    default void trigger() {
        publishAsync(computeObservationAsync());
    }

}
