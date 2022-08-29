package org.processmining.specpp.datastructures.encoding;

import org.processmining.specpp.traits.Copyable;
import org.processmining.specpp.traits.PartiallyOrdered;

import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class BitMask extends BitSet implements Copyable<BitMask>, SetQueries<BitMask>, MutatingSetOperations<BitMask>, PartiallyOrdered<BitMask> {

    @Override
    public boolean gt(BitMask other) {
        return isSupersetOf(other);
    }

    @Override
    public boolean lt(BitMask other) {
        return isSubsetOf(other);
    }

    public static BitMask completelySet(int exclusiveUpperLimit) {
        BitMask mask = new BitMask(exclusiveUpperLimit);
        mask.set(0, exclusiveUpperLimit);
        return mask;
    }

    public BitMask() {
    }

    public PrimitiveIterator.OfInt iterator() {
        return new BitSetIterator();
    }

    private class BitSetIterator implements PrimitiveIterator.OfInt {
        int next = nextSetBit(0);

        @Override
        public boolean hasNext() {
            return next != -1;
        }

        @Override
        public int nextInt() {
            if (next != -1) {
                int ret = next;
                next = nextSetBit(next + 1);
                return ret;
            } else {
                throw new NoSuchElementException();
            }
        }
    }

    public BitMask(int nbits) {
        super(nbits);
    }

    public static BitMask of(Integer... is) {
        BitMask bm = new BitMask();
        for (Integer i : is) {
            bm.set(i);
        }
        return bm;
    }

    public static BitMask of(IntStream is) {
        BitMask bm = new BitMask();
        is.forEach(bm::set);
        return bm;
    }

    @Override
    public BitMask copy() {
        BitMask result = new BitMask();
        stream().forEach(result::set);
        return result;
    }

    @Override
    public boolean intersects(BitMask other) {
        return super.intersects(other);
    }

    @Override
    public boolean setEquality(BitMask other) {
        return equals(other);
    }

    @Override
    public boolean isSubsetOf(BitMask other) {
        return stream().allMatch(other::get);
    }

    @Override
    public boolean isSupersetOf(BitMask other) {
        return other.stream().allMatch(this::get);
    }

    @Override
    public void union(BitMask other) {
        or(other);
    }

    @Override
    public void setminus(BitMask other) {
        andNot(other);
    }

    @Override
    public void intersection(BitMask other) {
        and(other);
    }

    public int kMaxIndex(int k) {
        if (k > cardinality()) return -1;
        int index = previousSetBit(length());
        for (int i = 1; i < k && index >= 0; i++) {
            index = previousSetBit(index - 1);
        }
        return index;
    }

    public int kMinIndex(int k) {
        if (k > cardinality()) return -1;
        int index = nextSetBit(0);
        for (int i = 1; i < k && index >= 0; i++) {
            index = nextSetBit(index + 1);
        }
        return index;
    }


}
