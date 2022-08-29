package org.processmining.specpp.datastructures.encoding;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An encoding with a range of type {@code Integer}. The range is densely packed within the nonnegative integers, i.e. there are no gaps.
 * This makes it suitable for storing precomputed orderings.
 *
 * @param <T> the encoding domain type
 */
public interface IntEncoding<T> extends Encoding<T, Integer>, HasDenseRange {

    int OUTSIDE_RANGE = -1;

    IntStream primitiveRange();

    @Override
    default Stream<Integer> range() {
        return primitiveRange().boxed();
    }

    boolean isIntInRange(int toDecode);

    @Override
    default boolean isInRange(Integer toDecode) {
        return isIntInRange(toDecode);
    }

}
