package org.processmining.specpp.datastructures.log;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.traits.Immutable;
import org.processmining.specpp.traits.Streamable;

import java.util.stream.IntStream;

public interface Log extends Iterable<IndexedVariant>, Streamable<IndexedVariant>, Immutable {

    int variantCount();

    int totalTraceCount();

    BitMask variantIndices();

    IntStream streamIndices();

    int getVariantFrequency(int index);

    Variant getVariant(int index);

    IntVector getVariantFrequencies();

}
