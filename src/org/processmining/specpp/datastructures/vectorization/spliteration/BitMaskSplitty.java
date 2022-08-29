package org.processmining.specpp.datastructures.vectorization.spliteration;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.vectorization.IntVectorStorage;

import java.nio.IntBuffer;

public class BitMaskSplitty extends AbstractBitMaskSplitty<IntBuffer> {


    public BitMaskSplitty(IntVectorStorage ivs, BitMask bitMask, int startIndex, int limit) {
        super(ivs, bitMask, startIndex, limit);
    }

    @Override
    protected IntBuffer make(int index) {
        return ivs.getVector(index);
    }

    @Override
    protected AbstractBitMaskSplitty<IntBuffer> makePrefix(int oldStart, int half) {
        return new BitMaskSplitty(ivs, bitMask, oldStart, half);
    }
}
