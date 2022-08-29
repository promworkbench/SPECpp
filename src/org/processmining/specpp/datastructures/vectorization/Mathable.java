package org.processmining.specpp.datastructures.vectorization;

import org.processmining.specpp.traits.Copyable;

public interface Mathable<T> {

    void add(T other);

    void subtract(T other);

    void negate();

    @SafeVarargs
    static <T extends Mathable<T> & Copyable<T>> T addition(T... ts) {
        assert ts.length >= 1;
        T result = ts[0].copy();
        for (int i = 1; i < ts.length; i++) {
            result.add(ts[i]);
        }
        return result;
    }

    @SafeVarargs
    static <T extends Mathable<T> & Copyable<T>> T subtraction(T... ts) {
        assert ts.length >= 1;
        T result = ts[0].copy();
        for (int i = 1; i < ts.length; i++) {
            result.subtract(ts[i]);
        }
        return result;
    }

}
