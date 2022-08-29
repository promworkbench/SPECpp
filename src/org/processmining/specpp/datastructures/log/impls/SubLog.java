package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IndexSubset;
import org.processmining.specpp.datastructures.log.Variant;

import java.util.stream.IntStream;

public class SubLog extends LogImpl {
    private final IndexSubset indexSubset;

    protected SubLog(IndexSubset indexSubset, Variant[] variants, int[] variantFrequencies) {
        super(variants, variantFrequencies);
        this.indexSubset = indexSubset;
    }

    @Override
    public int getVariantFrequency(int index) {
        return super.getVariantFrequency(indexSubset.mapIndex(index));
    }

    @Override
    public Variant getVariant(int index) {
        return super.getVariant(indexSubset.mapIndex(index));
    }

    @Override
    public IntStream streamIndices() {
        return indexSubset.streamIndices();
    }

    @Override
    public BitMask variantIndices() {
        return indexSubset.getIndices();
    }
}
