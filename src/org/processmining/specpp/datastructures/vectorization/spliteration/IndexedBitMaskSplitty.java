package org.processmining.specpp.datastructures.vectorization.spliteration;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.vectorization.IntVectorStorage;

import java.nio.IntBuffer;
import java.util.function.IntUnaryOperator;

public class IndexedBitMaskSplitty extends AbstractBitMaskSplitty<IndexedItem<IntBuffer>> {

    private final IntUnaryOperator outsideMapper;


    public IndexedBitMaskSplitty(IntVectorStorage ivs, BitMask bitMask, int startIndex, int limit, IntUnaryOperator outsideMapper) {
        super(ivs, bitMask, startIndex, limit);
        this.outsideMapper = outsideMapper;
    }

    @Override
    protected IndexedItem<IntBuffer> make(int index) {
        return new IndexedItem<>(outsideMapper.applyAsInt(index), ivs.getVector(index));
    }

    @Override
    protected AbstractBitMaskSplitty<IndexedItem<IntBuffer>> makePrefix(int oldStart, int half) {
        return new IndexedBitMaskSplitty(ivs, bitMask, oldStart, half, outsideMapper);
    }

}
