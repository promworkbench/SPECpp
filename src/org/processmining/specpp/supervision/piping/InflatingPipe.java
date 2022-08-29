package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.function.Function;

public class InflatingPipe<I extends Observation, O extends Observation> extends AbstractAsyncAwareObservable<O> implements ObservationPipe<I, O> {
    private final Function<I, Observations<O>> inflator;

    public InflatingPipe(Function<I, Observations<O>> inflator) {
        this.inflator = inflator;
    }

    protected Observations<O> inflate(I observation) {
        return inflator.apply(observation);
    }

    @Override
    public void observe(I observation) {
        for (O o : inflate(observation)) {
            publish(o);
        }
    }
}
