package org.processmining.specpp.datastructures.vectorization;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IndexSubset;
import org.processmining.specpp.traits.Copyable;

import java.util.Arrays;
import java.util.stream.IntStream;

public class IntVector implements Copyable<IntVector> {

    protected final int[] internal;
    protected final int total;


    protected IntVector(int[] internal, int sum) {
        this.internal = internal;
        total = sum;
    }

    public static IntVector of(int[] frequencies) {
        return new IntVector(Arrays.copyOf(frequencies, frequencies.length), Arrays.stream(frequencies).sum());
    }

    @Override
    public IntVector copy() {
        return new IntVector(Arrays.copyOf(internal, internal.length), total);
    }

    public IntStream view() {
        return Arrays.stream(internal, 0, internal.length);
    }

    public IntStream view(BitMask mask) {
        return mask.stream().map(this::get);
    }

    public int sum() {
        return view().sum();
    }

    public int sum(BitMask mask) {
        return view(mask).sum();
    }

    public int get(int index) {
        return internal[index];
    }

    public double getRelative(int index) {
        return get(index) / (double) total;
    }

    public int length() {
        return internal.length;
    }

    public int argMax() {
        int currentMax = -1, currentMaxIndex = -1;
        for (int i = 0; i < internal.length; i++) {
            if (internal[i] > currentMax) {
                currentMax = internal[i];
                currentMaxIndex = i;
            }
        }
        return currentMaxIndex;
    }

    public IntSubVector restrictTo(IndexSubset is) {
        int indexCount = is.getIndexCount();
        assert indexCount <= length();
        int[] arr = new int[indexCount];
        is.streamIndices().forEach(i -> {
            arr[is.mapIndex(i)] = get(i);
        });
        return new IntSubVector(is, arr, Arrays.stream(arr).sum());
    }

}
