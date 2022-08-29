package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.concurrent.CompletableFuture;

public class AsyncTransformingPipe<I extends Observation, O extends Observation> extends TransformingPipe<I, O> implements AsyncObservationPipe<I, O> {

    protected AsyncTransformingPipe(ObservationTransformer<? super I, ? extends O> transformer) {
        super(transformer);
    }

    @Override
    public void observeAsync(CompletableFuture<I> futureObservation) {
        publishAsync(futureObservation.thenApplyAsync(this::transform));
    }

}
