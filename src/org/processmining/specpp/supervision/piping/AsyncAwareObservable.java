package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncAwareObservable<O extends Observation> extends Observable<O> {


    List<AsyncObserver<O>> getAsyncObservers();

    List<Observer<O>> getNonAsyncObservers();

    default void publishFutureToAsyncObservers(CompletableFuture<O> futureObservation) {
        for (AsyncObserver<O> asyncObserver : getAsyncObservers()) {
            asyncObserver.observeAsync(futureObservation);
        }
    }

    default void publishFutureToNonAsyncObservers(CompletableFuture<O> futureObservation) {
        futureObservation.thenAcceptAsync(o -> {
            for (Observer<O> nonAsyncObserver : getNonAsyncObservers()) {
                nonAsyncObserver.observe(o);
            }
        });
    }

}
