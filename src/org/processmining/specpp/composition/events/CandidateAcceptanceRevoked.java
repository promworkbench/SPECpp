package org.processmining.specpp.composition.events;

import org.processmining.specpp.base.Candidate;

public class CandidateAcceptanceRevoked<C extends Candidate> extends CandidateCompositionEvent<C> {
    public CandidateAcceptanceRevoked(C candidate) {
        super(candidate, CompositionAction.RevokeAcceptance);
    }
}
