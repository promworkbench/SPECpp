package org.processmining.specpp.datastructures.util;

import java.util.Arrays;

public class EnumCounts<E extends Enum<E>> {

    public final int[] counts;

    public EnumCounts(int[] counts) {
        this.counts = counts;
    }

    public int getCount(E enumInstance) {
        return counts[enumInstance.ordinal()];
    }

    @Override
    public String toString() {
        return "EnumCounts{" + Arrays.toString(counts) + "}";
    }

}
