package org.processmining.specpp.datastructures.vectorization;

import org.processmining.specpp.datastructures.encoding.IndexSubset;

import java.util.Arrays;

public class IntSubVector extends IntVector {
    private final IndexSubset is;

    protected IntSubVector(IndexSubset is, int[] internal, int sum) {
        super(internal, sum);
        this.is = is;
    }

    public static IntSubVector of(IndexSubset is, int[] frequencies) {
        return new IntSubVector(is, Arrays.copyOf(frequencies, frequencies.length), Arrays.stream(frequencies).sum());
    }

    @Override
    public int get(int index) {
        return super.get(is.mapIndex(index));
    }

    @Override
    public IntVector copy() {
        return new IntSubVector(is, Arrays.copyOf(internal, internal.length), total);
    }

    @Override
    public IntSubVector restrictTo(IndexSubset indexSubset) {
        assert this.is.covers(indexSubset.getIndices());
        int[] arr = new int[indexSubset.getIndexCount()];
        indexSubset.streamIndices().forEach(i -> arr[indexSubset.mapIndex(i)] = get(i));
        return new IntSubVector(indexSubset, arr, Arrays.stream(arr).sum());
    }
}
