package org.processmining.specpp.datastructures.encoding;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class ConstantIntIntEncoding implements PrimitiveIntEncoding {

    private final int[] encodings;
    private final BitSet indicator;

    protected ConstantIntIntEncoding(int[] encodings, BitSet indicator) {
        this.encodings = encodings;
        this.indicator = indicator;
    }

    // sorted indices only
    public static ConstantIntIntEncoding of(IntStream ints, int max) {
        return of(ints.iterator(), max);
    }

    public static ConstantIntIntEncoding of(PrimitiveIterator.OfInt values, int maxValue) {
        int[] encodings = new int[maxValue + 1];
        BitSet indicator = new BitSet();
        int i = 0;
        int value, lastValue = 0;
        while (values.hasNext()) {
            value = values.nextInt();
            indicator.set(value);
            for (int j = lastValue + 1; j <= value; j++) {
                encodings[j] = i;
            }
            lastValue = value;
            i++;
        }
        return new ConstantIntIntEncoding(encodings, indicator);
    }


    @Override
    public int encodeInt(int item) {
        return encodings[item];
    }

    @Override
    public int decodeInt(int value) {
        int k = Arrays.binarySearch(encodings, value);
        // k = indicator.nextSetBit(k)
        while (k < encodings.length && encodings[k] == value) {
            k++;
        }
        return k - 1;
    }

    @Override
    public int size() {
        return indicator.cardinality();
    }

    @Override
    public IntStream primitiveDomain() {
        return indicator.stream();
    }

    @Override
    public IntStream primitiveRange() {
        return IntStream.rangeClosed(0, encodings[encodings.length - 1]);
    }

    @Override
    public boolean isIntInDomain(int toEncode) {
        return indicator.get(toEncode);
    }

    @Override
    public boolean isIntInRange(int toDecode) {
        return toDecode >= 0 && toDecode <= encodings[encodings.length - 1];
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstantIntIntEncoding that = (ConstantIntIntEncoding) o;

        if (!Arrays.equals(encodings, that.encodings)) return false;
        return Objects.equals(indicator, that.indicator);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(encodings);
        result = 31 * result + (indicator != null ? indicator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return Arrays.toString(encodings);
    }
}
