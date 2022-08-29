package org.processmining.specpp.datastructures.encoding;


/**
 * Specifies common mathematical queries on sets.
 *
 * @param <T>
 */
public interface SetQueries<T extends SetQueries<T>> {

    boolean intersects(T other);

    boolean setEquality(T other);

    boolean isSubsetOf(T other);

    default boolean isStrictSubsetOf(T other) {
        return !setEquality(other) && isSubsetOf(other);

    }

    boolean isSupersetOf(T other);

    default boolean isStrictSupersetOf(T other) {
        return !setEquality(other) && isSupersetOf(other);
    }

    default boolean isDisjoint(T other) {
        return !intersects(other);
    }

}
