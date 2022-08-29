package org.processmining.specpp.supervision.monitoring;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;

public class AccumulatingToIntWrapper<T> implements ToIntFunction<T> {
    private final ToIntFunction<T> toIntFunction;
    private final AtomicInteger atom;

    public AccumulatingToIntWrapper(ToIntFunction<T> deltaFunction) {
        this.toIntFunction = deltaFunction;
        atom = new AtomicInteger();
    }

    @Override
    public int applyAsInt(T value) {
        return atom.getAndAdd(toIntFunction.applyAsInt(value));
    }
}
