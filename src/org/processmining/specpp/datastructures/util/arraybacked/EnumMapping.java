package org.processmining.specpp.datastructures.util.arraybacked;

import java.util.Arrays;

public class EnumMapping<E extends Enum<E>, V>  {

    public final V[] enumValues;

    public EnumMapping(V[] enumValues) {
        this.enumValues = enumValues;
    }

    public V get(E enumInstance) {
        return enumValues[enumInstance.ordinal()];
    }

    @Override
    public String toString() {
        return "EnumMapping{" + Arrays.toString(enumValues) + "}";
    }


    public V[] getUnderlyingArr() {
        return this.enumValues;
    }
}
