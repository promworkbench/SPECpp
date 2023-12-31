package org.processmining.specpp.base;

/**
 * The base interface for a composer that also generates constraint events.
 *
 * @param <C> type of candidates that this composer handles
 * @param <I> type of intermediate result which is a composition of candidates
 * @param <R> type of the final result that is generated from the intermediate result (composition)
 * @param <L> type of constraint events generated by this composer
 * @see Composer
 * @see Constrainer
 */
public interface ConstrainingComposer<C extends Candidate, I extends Composition<C>, R extends Result, L extends ConstraintEvent> extends Composer<C, I, R>, Constrainer<L> {
}
