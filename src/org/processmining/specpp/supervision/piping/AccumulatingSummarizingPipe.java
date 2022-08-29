package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.transformers.AccumulatingSummarizer;
import org.processmining.specpp.traits.Mergeable;

import java.util.function.Supplier;

public class AccumulatingSummarizingPipe<O extends Observation & Mergeable<Mergeable>> extends SummarizingPipe<O> {
    public AccumulatingSummarizingPipe(Supplier<O> initial) {
        super(new AccumulatingSummarizer<>(initial));
    }
}
