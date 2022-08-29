package org.processmining.specpp.datastructures.encoding;

import com.google.common.collect.ImmutableBiMap;
import org.processmining.specpp.traits.ProperlyPrintable;

import java.util.Arrays;
import java.util.Comparator;

public class FixedOrdering<T> implements Comparator<T>, ProperlyPrintable {

    private final ImmutableBiMap<T, Integer> order;
    private final T[] arr;

    @SafeVarargs
    public FixedOrdering(T... ts) {
        ImmutableBiMap.Builder<T, Integer> builder = ImmutableBiMap.builder();
        for (int i = 0; i < ts.length; i++) {
            builder.put(ts[i], i);
        }
        order = builder.build();
        arr = Arrays.copyOf(ts, ts.length);
    }

    @Override
    public int compare(T o1, T o2) {
        if (!order.containsKey(o1) || !order.containsKey(o2)) throw new IncomparableException();
        int i = order.get(o1);
        int j = order.get(o2);
        return Integer.compare(i, j);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append("\u003C");
        }
        return sb.toString();
    }

    public static class IncomparableException extends RuntimeException {
    }
}
