package org.processmining.specpp.datastructures.vectorization.spliteration;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.util.IndexedItem;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface IndexedSpliterable<T> {

    Spliterator<IndexedItem<T>> indexedSpliterator();

    default Stream<IndexedItem<T>> indexedStream(boolean parallel) {
        return StreamSupport.stream(indexedSpliterator(), parallel);
    }

    Spliterator<IndexedItem<T>> indexedSpliterator(BitMask bitMask);

    default Stream<IndexedItem<T>> indexedStream(BitMask bitMask, boolean parallel) {
        return StreamSupport.stream(indexedSpliterator(bitMask), parallel);
    }

}
