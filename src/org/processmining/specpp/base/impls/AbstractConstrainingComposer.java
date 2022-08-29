package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.ConstrainingComposer;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.PipeWorks;

import java.util.function.Function;

/**
 * The abstract base class of a {@code ConstrainingComposer} for candidates of type {@code C}.
 * It internally employs a candidate collection of type {@code I} which serves as its growing intermediate result.
 * The final result of type {@code R} can be computed on demand.
 * <p>
 * This class participates in the componenting system to provide {@code CandidateConstraint} events.
 *
 * @param <C>
 * @param <I>
 * @param <R>
 * @see ConstrainingComposer
 * @see CandidateConstraint
 */
public abstract class AbstractConstrainingComposer<C extends Candidate, I extends AdvancedComposition<C>, R extends Result, L extends CandidateConstraint<C>> extends AbstractComposer<C, I, R> implements ConstrainingComposer<C, I, R, L> {

    private final EventSupervision<L> constraintOutput = PipeWorks.eventSupervision();

    public AbstractConstrainingComposer(I composition, Function<? super I, R> assembleResult) {
        super(composition, assembleResult);
    }

    protected void publishConstraint(L constraint) {
        constraintOutput.publish(constraint);
    }

    @Override
    public Observable<L> getConstraintPublisher() {
        return constraintOutput;
    }

}

