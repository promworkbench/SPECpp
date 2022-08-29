package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

public class SummarizingPipe<O extends Observation> extends DeflatingPipe<O, O> {

    public SummarizingPipe(ObservationSummarizer<O, O> summarizer) {
        super(summarizer);
    }

}
