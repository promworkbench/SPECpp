package org.processmining.specpp.datastructures.util;

import org.processmining.specpp.traits.Immutable;

public class ImmutableTuple2<T1, T2> implements Immutable, Tuple2<T1, T2> {

    protected final T1 t1;
    protected final T2 t2;

    public ImmutableTuple2(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public T1 getT1() {
        return t1;
    }

    @Override
    public T2 getT2() {
        return t2;
    }

    @Override
    public String toString() {
        return "(" + t1 + ", " + t2 + ")";
    }
}
