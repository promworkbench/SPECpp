package org.processmining.specpp.datastructures.encoding;

import org.processmining.specpp.traits.Copyable;
import org.processmining.specpp.traits.PartiallyOrdered;

import java.util.stream.IntStream;

public class IndexSubset implements SetQueries<IndexSubset>, Copyable<IndexSubset>, PartiallyOrdered<IndexSubset> {
    private final PrimitiveIntEncoding indexMapping;
    private final BitMask subset;

    public IndexSubset(BitMask subset, PrimitiveIntEncoding subMapping) {
        this.subset = subset;
        this.indexMapping = subMapping;
    }

    public static IndexSubset complete(int size) {
        return new IndexSubset(BitMask.completelySet(size), new IdentityEncoding(0, size - 1));
    }

    public static IndexSubset of(BitMask mask) {
        return new IndexSubset(mask, ConstantIntIntEncoding.of(mask.stream(), mask.kMaxIndex(1)));
    }

    @Override
    public IndexSubset copy() {
        return new IndexSubset(subset.copy(), indexMapping);
    }

    public int mapIndex(int index) {
        return indexMapping.encodeInt(index);
    }

    public int unmapIndex(int index) {
        return indexMapping.decodeInt(index);
    }

    public boolean contains(int index) {
        return subset.get(index);
    }

    public IntStream streamIndices() {
        return subset.stream();
    }

    public IntStream streamMappingRange() {
        return indexMapping.encodeIntStream(streamIndices());
    }

    public BitMask getIndices() {
        return subset;
    }

    public IntStream mapIndices(IntStream ints) {
        return indexMapping.encodeIntStream(ints);
    }

    public IntStream unmapIndices(IntStream ints) {
        return indexMapping.decodeIntStream(ints);
    }

    public BitMask mapIndices(BitMask indices) {
        BitMask mask = new BitMask();
        indices.stream().filter(this::contains).forEach(mask::set);
        return mask;
    }

    public BitMask unmapIndices(BitMask indices) {
        BitMask mask = new BitMask();
        unmapIndices(indices.stream()).forEach(mask::set);
        return mask;
    }

    public PrimitiveIntEncoding getIndexMapping() {
        return indexMapping;
    }

    public BitMask indexIntersection(IndexSubset other) {
        return MutatingSetOperations.intersection(subset, other.subset);
    }

    @Override
    public boolean intersects(IndexSubset other) {
        return subset.intersects(other.subset);
    }

    @Override
    public boolean setEquality(IndexSubset other) {
        return subset.setEquality(other.subset);
    }

    @Override
    public boolean isSubsetOf(IndexSubset other) {
        return subset.isSubsetOf(other.subset);
    }

    @Override
    public boolean isSupersetOf(IndexSubset other) {
        return subset.isSupersetOf(other.subset);
    }

    public boolean covers(BitMask indicatorMask) {
        return subset.isSupersetOf(indicatorMask);
    }

    @Override
    public boolean gt(IndexSubset other) {
        return isSupersetOf(other);
    }

    @Override
    public boolean lt(IndexSubset other) {
        return isSubsetOf(other);
    }

    public int getIndexCount() {
        return subset.cardinality();
    }


    @Override
    public String toString() {
        return "IndexSubset{" + subset + "}";
    }
}
