package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.componenting.system.link.ComposerComponent;

import java.util.function.Function;

/**
 * The abstract base class for composers which internally manages an advanced, i.e. mutable, composition component.
 *
 * @param <C>
 * @param <I>
 * @param <R>
 */
public abstract class AbstractComposer<C extends Candidate, I extends AdvancedComposition<C>, R extends Result> extends AbstractBaseClass implements ComposerComponent<C, I, R> {

    protected final Function<? super I, R> assembleResult;
    protected final I composition;

    /**
     * @param composition    the composition to manage
     * @param assembleResult a method to assemble the result from the candidate collection
     */
    public AbstractComposer(I composition, Function<? super I, R> assembleResult) {
        this.composition = composition;
        this.assembleResult = assembleResult;
        registerSubComponent(composition);
    }

    /**
     * Delegates the decision on whether to accept a candidate to {@code deliberateAcceptance()}.
     *
     * @param candidate the candidate to be handled
     */
    @Override
    public void accept(C candidate) {
        if (deliberateAcceptance(candidate)) {
            acceptCandidate(candidate);
        } else rejectCandidate(candidate);
    }

    protected I composition() {
        return composition;
    }

    /**
     * Hook method where the acceptance decision is made.
     *
     * @param candidate the candidate to decide acceptance for
     * @return whether to accept this candidate
     */
    protected abstract boolean deliberateAcceptance(C candidate);


    protected final void acceptCandidate(C candidate) {
        composition.accept(candidate);
        candidateAccepted(candidate);
    }


    protected final void rejectCandidate(C candidate) {
        candidateRejected(candidate);
    }

    protected final void revokeAcceptance(C candidate) {
        composition.remove(candidate);
        acceptanceRevoked(candidate);
    }

    protected final void revokeLastAcceptance() {
        C last = composition.removeLast();
        acceptanceRevoked(last);
    }

    /**
     * Hook for actions to perform on initially accepted candidates which are now removed.
     *
     * @param candidate the revoked candidate
     */
    protected abstract void acceptanceRevoked(C candidate);

    /**
     * Hook for actions to perform on accepted candidates.
     *
     * @param candidate the accepted candidate
     */
    protected abstract void candidateAccepted(C candidate);

    /**
     * Hook for actions to perform on rejected candidates.
     *
     * @param candidate the rejected candidate
     */
    protected abstract void candidateRejected(C candidate);

    @Override
    public boolean isFinished() {
        return !composition.hasCapacityLeft();
    }

    @Override
    public final I getIntermediateResult() {
        return composition;
    }

    @Override
    public final R generateResult() {
        return assembleResult.apply(composition);
    }
}
