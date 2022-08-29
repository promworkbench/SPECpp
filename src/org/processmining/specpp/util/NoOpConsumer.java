package org.processmining.specpp.util;

import java.util.function.Consumer;

public class NoOpConsumer<T> implements Consumer<T> {

    @Override
    public void accept(T t) {

    }

}
