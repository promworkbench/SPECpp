package org.processmining.specpp.base;

import java.util.function.Consumer;


/**
 * Base interface for Composers.
 * They function as Consumers as the pendant to {@code Proposer}s which are Suppliers.
 * A composer maintains an internal collection of candidates. This intermediate state may be queried asynchronously via {@code getIntermediateResult()}
 * Candidates are offered to this class via the {@code accept(Candidate c)} method.
 * When
 * (a) the candidate source (proposer) is exhausted,
 * (b) or this composer signals {@code isFinished()}, e.g. due to its internal composition being full,
 * (c) or the computation is gracefully cancelled,
 * {@code candidatesAreExhausted()} is called.
 * At the end of the composer's lifecycle, {@code generateResult()} is called.
 * The collection of candidates can be transformed, e.g. summarized, in this hook method.
 *
 * @param <C> type of candidates that this composer handles
 * @param <I> type of intermediate result which is a composition of candidates
 * @param <R> type of the final result that is generated from the intermediate result (composition)
 */
public interface Composer<C extends Candidate, I extends Composition<C>, R extends Result> extends Consumer<C> {

    /**
     * Whether this composer has reached a state where it does not want to handle new candidates.
     *
     * @return true iff no more candidates will be accepted
     */
    boolean isFinished();

    /**
     * Hook method which is called when this composer will not receive any more candidates in the future as the connected proposer has been exhausted.
     */
    void candidatesAreExhausted();

    /**
     * Typically just returns the internally managed composition.
     * Care must be taken, that this method may be called asynchronously.
     *
     * @return the intermediate result
     */
    I getIntermediateResult();

    /**
     * Hook method where a final result is computed from the incrementally built up intermediate result.
     *
     * @return the final result
     */
    R generateResult();

    /**
     * Hook method which decides how to handle an incoming candidate.
     * The internal logic decides whether the candidate is eventually added to the current composition.
     *
     * @param c the candidate that has been offered to this composer
     */
    @Override
    void accept(C c);
}
