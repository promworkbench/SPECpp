package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

public class TransformingPipe<I extends Observation, O extends Observation> extends AbstractAsyncAwareObservable<O> implements ObservationPipe<I, O> {

    private final ObservationTransformer<? super I, ? extends O> transformer;

    public TransformingPipe(ObservationTransformer<? super I, ? extends O> transformer) {
        this.transformer = transformer;
    }

    public O transform(I observation) {
        return transformer.apply(observation);
    }


    @Override
    public void observe(I observation) {
        publish(transform(observation));
    }

    @Override
    public String toString() {
        return "TransformingPipe(" + transformer.toString() + ")";
    }

}
