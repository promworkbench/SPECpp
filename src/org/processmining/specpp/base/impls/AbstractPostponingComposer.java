package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.ConstrainingComposer;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.traits.Triggerable;

public abstract class AbstractPostponingComposer<C extends Candidate, I extends CompositionComponent<C>, R extends Result, L extends CandidateConstraint<C>> extends RecursiveComposer<C, I, R> implements ConstrainingComposer<C, I, R, L>, Triggerable {

    private final EventSupervision<L> constraintOutput = PipeWorks.eventSupervision();

    public AbstractPostponingComposer(ComposerComponent<C, I, R> childComposer) {
        super(childComposer);
    }

    protected final void publishConstraint(L constraint) {
        constraintOutput.publish(constraint);
    }

    @Override
    public final Observable<L> getConstraintPublisher() {
        return constraintOutput;
    }

    @Override
    public void accept(C candidate) {
        CandidateDecision candidateDecision = deliberateCandidate(candidate);
        switch (candidateDecision) {
            case Accept:
                acceptCandidate(candidate);
                break;
            case Reject:
                rejectCandidate(candidate);
                break;
            case Discard:
                discardCandidate(candidate);
                break;
            case Postpone:
                postponeDecision(candidate);
                break;
        }
    }


    public enum CandidateDecision {
        Accept, Reject, Discard, Postpone;
    }

    protected abstract CandidateDecision deliberateCandidate(C candidate);


    /**
     * Hook method to define one iteration of postponed decision traversal.
     * This may be called multiple times.
     *
     * @return true if the set of postponed decisions changed
     */
    protected abstract boolean handlePostponedDecisions();

    protected int handlePostponedDecisionsUntilNoChange() {
        int limit = 100;
        int count = 0;
        while (count++ < limit && handlePostponedDecisions()) ;
        return count;
    }

    protected abstract void postponeDecision(C candidate);

    protected void acceptCandidate(C candidate) {
        forward(candidate);
    }

    protected abstract void rejectCandidate(C candidate);

    protected abstract void discardCandidate(C candidate);

    @Override
    public final void trigger() {
        handlePostponedDecisionsUntilNoChange();
    }

    @Override
    public void candidatesAreExhausted() {
        handlePostponedDecisionsUntilNoChange();
        super.candidatesAreExhausted();
    }

}
