package org.processmining.specpp.config.components;

import java.util.function.Function;

@FunctionalInterface
public interface InitializingBuilder<T, A> extends Function<A, T> {

    T build(A a);

    @Override
    default T apply(A a) {
        return build(a);
    }
}
