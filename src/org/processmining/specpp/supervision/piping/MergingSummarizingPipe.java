package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.transformers.MergingSummarizer;
import org.processmining.specpp.traits.Mergeable;

public class MergingSummarizingPipe<O extends Observation & Mergeable<? super O>> extends SummarizingPipe<O> {
    public MergingSummarizingPipe() {
        super(new MergingSummarizer<>());
    }
}
