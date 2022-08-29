package org.processmining.specpp.supervision.transformers;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.ObservationSummarizer;
import org.processmining.specpp.supervision.piping.Observations;
import org.processmining.specpp.traits.Mergeable;

import java.util.function.Supplier;

public class AccumulatingSummarizer<O extends Observation & Mergeable<? super O>> implements ObservationSummarizer<O, O> {

    private final O accumulator;

    public AccumulatingSummarizer(Supplier<O> initial) {
        this.accumulator = initial.get();
    }

    @Override
    public O summarize(Observations<? extends O> observations) {
        for (O observation : observations) {
            accumulator.merge(observation);
        }
        return accumulator;
    }

}
