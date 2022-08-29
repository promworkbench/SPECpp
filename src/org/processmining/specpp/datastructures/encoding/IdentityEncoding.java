package org.processmining.specpp.datastructures.encoding;

import java.util.stream.IntStream;

public class IdentityEncoding implements PrimitiveIntEncoding {

    private final int low, high;

    public IdentityEncoding(int low, int high) {
        this.low = low;
        this.high = high;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentityEncoding that = (IdentityEncoding) o;

        if (low != that.low) return false;
        return high == that.high;
    }

    @Override
    public int hashCode() {
        int result = low;
        result = 31 * result + high;
        return result;
    }

    @Override
    public int encodeInt(int i) {
        return i;
    }

    @Override
    public int decodeInt(int i) {
        return i;
    }


    @Override
    public int size() {
        return high - low;
    }

    @Override
    public IntStream primitiveDomain() {
        return IntStream.rangeClosed(low, high);
    }

    @Override
    public IntStream primitiveRange() {
        return IntStream.rangeClosed(low, high);
    }

    @Override
    public boolean isIntInDomain(int toEncode) {
        return low <= toEncode && toEncode <= high;
    }

    @Override
    public boolean isIntInRange(int toDecode) {
        return low <= toDecode && toDecode <= high;
    }
}
