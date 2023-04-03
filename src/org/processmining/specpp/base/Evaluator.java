package org.processmining.specpp.base;

import java.util.function.Function;

/**
 * General interface for evaluators. Essentially a function restricted to {@code Evaluable} and {@code Evaluation} types.
 * E.g. {@code Place} to {@code BasicFitnessEvaluation}.
 *
 * @param <I> evaluable type
 * @param <E> evaluation type
 */
public interface Evaluator<I extends Evaluable, E extends Evaluation> extends Function<I, E> {

    E eval(I input);

    @Override
    default E apply(I i) {
        return eval(i);
    }
}
