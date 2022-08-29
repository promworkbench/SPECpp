package org.processmining.specpp.componenting.data;

import java.util.function.Supplier;

@FunctionalInterface
public interface DataSource<T> extends Supplier<T> {

    T getData();

    @Override
    default T get() {
        return getData();
    }
}
