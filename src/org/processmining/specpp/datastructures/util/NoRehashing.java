package org.processmining.specpp.datastructures.util;

import org.processmining.specpp.traits.Immutable;
import org.processmining.specpp.traits.ProperlyHashable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * A utility base class that precomputes and caches the {@code hashCode()} computation and overrides {@code equals}.
 * It meets the semantic contract that two subclassing objects of the same type are equal iff their specified {@code internal} objects are equal.
 * If {@code T} is an array type, hashcode and equality are computed element wise.
 *
 * @param <T>
 * @see #internal
 */
public abstract class NoRehashing<T> implements Immutable, ProperlyHashable {

    /**
     * The specified object that the {@code hashCode} and equality is based on.
     * It is defined by subclasses.
     */
    protected final T internal;
    /**
     * The precomputed and cached {@code hashCode()} result.
     */
    private final int hash;
    /**
     * The predicate to compare equality based on {@code internal}.
     * Used to store the appropriate method to compare two objects of type {@code T} in case they are arrays.
     */
    private final BiPredicate<T, Object> equality;

    public NoRehashing(T internal) {
        assert internal != null;
        this.internal = internal;
        boolean isArr = internal.getClass().isArray();

        this.hash = isArr ? Arrays.hashCode((Object[]) internal) : internal.hashCode();
        BiPredicate<T, Object> arrEq = (t1, t2) -> Arrays.equals((Object[]) t1, (Object[]) t2);
        this.equality = isArr ? arrEq : Objects::equals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoRehashing<?> that = (NoRehashing<?>) o;

        return equality.test(internal, that.internal);
    }

    @Override
    public int hashCode() {
        return hash;
    }


}
