package org.processmining.specpp.composition.events;

import org.processmining.specpp.base.Candidate;

public class CandidateAccepted<C extends Candidate> extends CandidateCompositionEvent<C> {
    public CandidateAccepted(C candidate) {
        super(candidate, CompositionAction.Accept);
    }
}
