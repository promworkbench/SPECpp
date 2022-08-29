package org.processmining.specpp.datastructures.util;

import org.processmining.specpp.datastructures.encoding.BitMask;

import java.util.Arrays;

public class EnumBitMasks<E extends Enum<E>> {

    public final BitMask[] bitMasks;


    public EnumBitMasks(BitMask[] bitMasks) {
        this.bitMasks = bitMasks;
    }

    public BitMask getBitMask(E enumInstance) {
        return bitMasks[enumInstance.ordinal()];
    }

    @Override
    public String toString() {
        return "EnumBitMasks{" + Arrays.toString(bitMasks) + "}";
    }
}
