package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractQueueingComposer<C extends Candidate, I extends CompositionComponent<C>, R extends Result, L extends CandidateConstraint<C>> extends AbstractPostponingComposer<C, I, R, L> {

    protected List<C> postponedCandidates;

    public AbstractQueueingComposer(ComposerComponent<C, I, R> childComposer) {
        super(childComposer);
    }

    @Override
    protected void initSelf() {
        postponedCandidates = new LinkedList<>();
    }

    @Override
    protected void postponeDecision(C candidate) {
        postponedCandidates.add(candidate);
    }

    protected abstract CandidateDecision reDeliberateCandidate(C candidate);

    @Override
    protected boolean iteratePostponedCandidates() {
        LinkedList<C> postponedAgain = new LinkedList<>();
        for (C postponedCandidate : postponedCandidates) {
            CandidateDecision candidateDecision = reDeliberateCandidate(postponedCandidate);
            switch (candidateDecision) {
                case Accept:
                    acceptCandidate(postponedCandidate);
                    break;
                case Reject:
                    rejectCandidate(postponedCandidate);
                    break;
                case Discard:
                    discardCandidate(postponedCandidate);
                    break;
                case Postpone:
                    postponedAgain.add(postponedCandidate);
                    break;
            }
        }
        boolean hasChanged = postponedCandidates.size() != postponedAgain.size();
        postponedCandidates = postponedAgain;
        return hasChanged;
    }


}
