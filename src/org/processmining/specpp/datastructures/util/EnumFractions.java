package org.processmining.specpp.datastructures.util;

import java.util.Arrays;

public class EnumFractions<E extends Enum<E>> {

    protected final double[] fractions;


    public EnumFractions(double[] fractions) {
        this.fractions = fractions;
    }

    public double getFraction(E enumInstance) {
        return fractions[enumInstance.ordinal()];
    }

    protected double[] underlyingArr() {
        return fractions;
    }

    @Override
    public String toString() {
        return "EnumFractions(" + Arrays.toString(fractions) + ")";
    }

}
