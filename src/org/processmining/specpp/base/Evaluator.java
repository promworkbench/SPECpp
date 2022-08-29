package org.processmining.specpp.base;

import java.util.function.Function;

public interface Evaluator<I extends Evaluable, E extends Evaluation> extends Function<I, E> {

    E eval(I input);

    @Override
    default E apply(I i) {
        return eval(i);
    }
}
