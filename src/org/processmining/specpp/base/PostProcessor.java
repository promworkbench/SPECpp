package org.processmining.specpp.base;

import java.util.function.Function;

public interface PostProcessor<S extends Result, T extends Result> extends Function<S, T> {

    T postProcess(S result);

    default String getLabel() {
        return getClass().getSimpleName();
    }


    Class<S> getInputClass();

    Class<T> getOutputClass();

    @Override
    default T apply(S s) {
        return postProcess(s);
    }
}
