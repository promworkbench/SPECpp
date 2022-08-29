package org.processmining.specpp.datastructures.vectorization.spliteration;

import org.processmining.specpp.datastructures.encoding.BitMask;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Spliterable<T> {

    Spliterator<T> spliterator();

    default Stream<T> stream(boolean parallel) {
        return StreamSupport.stream(spliterator(), parallel);
    }

    Spliterator<T> spliterator(BitMask bitMask);

    default Stream<T> stream(BitMask bitMask, boolean parallel) {
        return StreamSupport.stream(spliterator(bitMask), parallel);
    }

}
