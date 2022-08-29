package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.function.Predicate;

public class PredicatePipe<O extends Observation> extends IdentityPipe<O> {

    private final Predicate<? super O> predicate;

    public PredicatePipe(Predicate<? super O> predicate) {
        this.predicate = predicate;
    }

    @Override
    public void observe(O observation) {
        if (predicate.test(observation)) publish(observation);
    }
}
