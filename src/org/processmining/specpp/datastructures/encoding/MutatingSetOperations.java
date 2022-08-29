package org.processmining.specpp.datastructures.encoding;

import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.traits.Copyable;

/**
 * Specifies mutating set operations on sets.
 * The static definitions allow non mutating application of these methods on copyable implementations.
 *
 * @param <T>
 */
public interface MutatingSetOperations<T extends MutatingSetOperations<T>> {


    void union(T other);

    void setminus(T other);

    void intersection(T other);

    @SafeVarargs
    static <T extends MutatingSetOperations<T> & Copyable<T>> T union(T... sets) {
        assert sets.length >= 1;
        T result = sets[0].copy();
        for (int i = 1; i < sets.length; i++) {
            result.union(sets[i]);
        }
        return result;
    }

    @SafeVarargs
    static <T extends MutatingSetOperations<T> & Copyable<T>> T setminus(T... sets) {
        assert sets.length >= 1;
        T result = sets[0].copy();
        for (int i = 1; i < sets.length; i++) {
            result.setminus(sets[i]);
        }
        return result;
    }

    static <T extends MutatingSetOperations<T> & Copyable<T>> Pair<T> dualSetminus(T s1, T s2) {
        T r1 = s1.copy();
        T r2 = s2.copy();
        r1.setminus(s2);
        r2.setminus(s1);
        return new ImmutablePair<>(r1, r2);
    }

    static <T extends MutatingSetOperations<T> & Copyable<T>> Pair<T> dualIntersection(T s1, T s2) {
        T r1 = s1.copy();
        T r2 = s2.copy();
        r1.intersection(s2);
        r2.intersection(s1);
        return new ImmutablePair<>(r1, r2);
    }

    @SafeVarargs
    static <T extends MutatingSetOperations<T> & Copyable<T>> T intersection(T... sets) {
        assert sets.length >= 1;
        T result = sets[0].copy();
        for (int i = 1; i < sets.length; i++) {
            result.intersection(sets[i]);
        }
        return result;
    }

}
