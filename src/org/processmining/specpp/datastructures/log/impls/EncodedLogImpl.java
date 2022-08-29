package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.datastructures.vectorization.IntVectorStorage;

import java.nio.IntBuffer;
import java.util.Spliterator;
import java.util.stream.IntStream;

public class EncodedLogImpl implements EncodedLog {
    private final IntVector variantFrequencies;
    protected final IntEncoding<Activity> encoding;
    protected final IntVectorStorage ivs;

    public EncodedLogImpl(IntVector variantFrequencies, IntVectorStorage ivs, IntEncoding<Activity> encoding) {
        assert variantFrequencies.length() == ivs.getVectorCount();
        this.variantFrequencies = variantFrequencies;
        this.encoding = encoding;
        this.ivs = ivs;
    }

    @Override
    public IntEncoding<Activity> getEncoding() {
        return encoding;
    }

    @Override
    public IntVectorStorage getEncodedVariantVectors() {
        return ivs;
    }

    @Override
    public IntVector getVariantFrequencies() {
        return variantFrequencies;
    }

    @Override
    public int getVariantFrequency(int index) {
        return variantFrequencies.get(index);
    }

    @Override
    public IntBuffer getEncodedVariant(int index) {
        return ivs.getVector(index);
    }

    @Override
    public IntStream streamIndices() {
        return ivs.indexStream();
    }

    @Override
    public BitMask variantIndices() {
        return BitMask.of(streamIndices());
    }

    @Override
    public int variantCount() {
        return ivs.getVectorCount();
    }

    @Override
    public int totalTraceCount() {
        return variantFrequencies.sum();
    }

    @Override
    public Spliterator<IntBuffer> spliterator() {
        return ivs.spliterator();
    }

    @Override
    public Spliterator<IntBuffer> spliterator(BitMask bitMask) {
        return ivs.spliterator(bitMask);
    }

    @Override
    public Spliterator<IndexedItem<IntBuffer>> indexedSpliterator() {
        return ivs.indexedSpliterator();
    }

    @Override
    public Spliterator<IndexedItem<IntBuffer>> indexedSpliterator(BitMask bitMask) {
        return ivs.indexedSpliterator(bitMask);
    }

}
