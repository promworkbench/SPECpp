package org.processmining.specpp.config.components;

import java.util.function.Supplier;

@FunctionalInterface
public interface SimpleBuilder<T> extends Supplier<T> {

    T build();

    @Override
    default T get() {
        return build();
    }
}
