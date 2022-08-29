package org.processmining.specpp.datastructures.vectorization.spliteration;

import org.processmining.specpp.datastructures.vectorization.IntVectorStorage;

import java.nio.IntBuffer;

public class Splitty extends AbstractSplitty<IntBuffer> {


    public Splitty(IntVectorStorage intVectorStorage, int startVectorIndex, int fenceVectorIndex) {
        super(intVectorStorage, startVectorIndex, fenceVectorIndex);
    }

    @Override
    protected IntBuffer make(int index) {
        return ivs.getVector(index);
    }

    @Override
    protected AbstractSplitty<IntBuffer> makePrefix(int low, int mid) {
        return new Splitty(ivs, low, mid);
    }
}
