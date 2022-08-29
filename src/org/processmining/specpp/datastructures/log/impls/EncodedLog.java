package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.datastructures.vectorization.IntVectorStorage;
import org.processmining.specpp.datastructures.vectorization.spliteration.IndexedSpliterable;
import org.processmining.specpp.datastructures.vectorization.spliteration.Spliterable;

import java.nio.IntBuffer;
import java.util.stream.IntStream;

public interface EncodedLog extends Spliterable<IntBuffer>, IndexedSpliterable<IntBuffer> {
    IntEncoding<Activity> getEncoding();

    IntVectorStorage getEncodedVariantVectors();

    IntVector getVariantFrequencies();

    int getVariantFrequency(int index);

    IntBuffer getEncodedVariant(int index);

    IntStream streamIndices();

    BitMask variantIndices();

    int variantCount();

    int totalTraceCount();

}
