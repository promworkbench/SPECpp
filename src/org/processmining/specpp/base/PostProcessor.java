package org.processmining.specpp.base;

import java.util.function.Function;

/**
 * General Interface for a post-processing operation which transforms an object of type {@code S} to {@code T}.
 * It additionally provides a human-readable label, as well as instances of the classes of supported types.
 *
 * @param <S> input type
 * @param <T> output type
 */
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
