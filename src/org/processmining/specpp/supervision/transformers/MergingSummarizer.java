package org.processmining.specpp.supervision.transformers;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.ObservationSummarizer;
import org.processmining.specpp.supervision.piping.Observations;
import org.processmining.specpp.traits.Mergeable;

public class MergingSummarizer<O extends Observation & Mergeable<? super O>> implements ObservationSummarizer<O, O> {

    @Override
    public O summarize(Observations<? extends O> observations) {
        O accumulator = null;
        for (O observation : observations) {
            if (accumulator == null) accumulator = observation;
            else accumulator.merge(observation);
        }
        return accumulator;
    }

}
