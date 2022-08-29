package org.processmining.specpp.datastructures.encoding;

import org.processmining.specpp.traits.Copyable;
import org.processmining.specpp.traits.ProperlyHashable;
import org.processmining.specpp.traits.ProperlyPrintable;

import java.util.Arrays;
import java.util.stream.IntStream;

public class IntIntEncoding implements PrimitiveIntEncoding, ProperlyHashable, ProperlyPrintable, Copyable<IntIntEncoding> {

    private final int[] internal;

    public IntIntEncoding(int[] sortedIntegers) {
        internal = sortedIntegers;
    }

    public IntIntEncoding of(IntStream ints) {
        return new IntIntEncoding(ints.toArray());
    }

    @Override
    public int size() {
        return internal.length;
    }

    @Override
    public IntStream primitiveDomain() {
        return Arrays.stream(internal);
    }

    @Override
    public int encodeInt(int item) {
        return Arrays.binarySearch(internal, item);
    }

    @Override
    public int decodeInt(int value) {
        return internal[value];
    }

    @Override
    public IntStream primitiveRange() {
        return IntStream.range(0, internal.length);
    }

    @Override
    public boolean isIntInDomain(int toEncode) {
        return Arrays.binarySearch(internal, toEncode) >= 0;
    }

    @Override
    public boolean isIntInRange(int toDecode) {
        return 0 <= toDecode && toDecode < internal.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(internal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntIntEncoding that = (IntIntEncoding) o;

        return Arrays.equals(internal, that.internal);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(internal);
    }

    @Override
    public IntIntEncoding copy() {
        return new IntIntEncoding(Arrays.copyOf(internal, internal.length));
    }
}
