package org.processmining.specpp.base;

import java.util.function.Consumer;


/**
 * @param <C>
 * @param <I>
 * @param <R>
 */
public interface Composer<C extends Candidate, I extends Composition<C>, R extends Result> extends Consumer<C> {

    boolean isFinished();

    void candidatesAreExhausted();

    I getIntermediateResult();

    R generateResult();

    @Override
    void accept(C c);
}
