package org.processmining.specpp.base;

import org.processmining.specpp.base.impls.PostProcessorPipe;

import java.util.function.Function;

public interface PostProcessor<S extends Result, T extends Result> extends Function<S, T> {

    T postProcess(S result);

    default String getLabel() {
        return getClass().getSimpleName();
    }

    default <V extends Result> PostProcessor<V, T> compose(PostProcessor<V, ? extends S> before) {
        return new PostProcessorPipe<>(before, this);
    }

    @Override
    default T apply(S s) {
        return postProcess(s);
    }
}
