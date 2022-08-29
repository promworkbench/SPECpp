package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.concurrent.CompletableFuture;

public interface AsyncObserver<O extends Observation> extends Observer<O> {

    void observeAsync(CompletableFuture<O> futureObservation);


}
