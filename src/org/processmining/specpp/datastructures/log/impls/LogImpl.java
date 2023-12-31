package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.traits.ProperlyPrintable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LogImpl implements Log, ProperlyPrintable {

    private final Variant[] variants;
    private final IntVector variantFrequencies;
    private final int absoluteSize;

    protected LogImpl(Variant[] variants, int[] variantFrequencies) {
        this.variants = variants;
        this.variantFrequencies = IntVector.of(variantFrequencies);
        this.absoluteSize = Arrays.stream(variantFrequencies).sum();
    }


    @Override
    public Iterator<IndexedVariant> iterator() {
        return stream().iterator();
    }

    @Override
    public Stream<IndexedVariant> stream() {
        PrimitiveIterator.OfInt indices = streamIndices().iterator();
        Spliterators.AbstractSpliterator<IndexedVariant> spliterator = new Spliterators.AbstractSpliterator<IndexedVariant>(variants.length, Spliterator.SIZED | Spliterator.ORDERED | Spliterator.NONNULL) {

            int i = 0;

            @Override
            public boolean tryAdvance(Consumer<? super IndexedVariant> action) {
                if (i < variants.length)
                    action.accept(new IndexedVariant(indices.nextInt(), variants[i]));
                return ++i < variants.length;
            }

        };
        return StreamSupport.stream(spliterator, false);
    }

    @Override
    public int variantCount() {
        return variants.length;
    }

    @Override
    public int totalTraceCount() {
        return absoluteSize;
    }

    @Override
    public BitMask variantIndices() {
        return BitMask.completelySet(variants.length);
    }

    @Override
    public IntStream streamIndices() {
        return IntStream.range(0, variants.length);
    }

    @Override
    public int getVariantFrequency(int index) {
        return variantFrequencies.get(index);
    }

    @Override
    public Variant getVariant(int index) {
        return variants[index];
    }

    @Override
    public IntVector getVariantFrequencies() {
        return variantFrequencies;
    }

    public String fullString() {
        return Arrays.toString(variants);
    }

    @Override
    public String toString() {
        return "Log{" + "|V|=" + variantCount() + ", |L|=" + totalTraceCount() + ", most common variant=" + getVariant(getVariantFrequencies().argMax()) + "}";
    }
}
