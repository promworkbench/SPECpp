package org.processmining.specpp.datastructures.util.arraybacked;

import java.util.Arrays;

public class EnumFractions<E extends Enum<E>> {

    protected final double[] fractions;

    public EnumFractions(double[] fractions) {
        this.fractions = fractions;
    }

    public double getFraction(E enumInstance) {
        return fractions[enumInstance.ordinal()];
    }

    @Override
    public String toString() {
        return "EnumFractions{" + Arrays.toString(fractions) + "}";
    }

    public void setFraction(E outcome, double fraction) {
        this.fractions[outcome.ordinal()] = fraction;
    }

    public double[] getUnderlyingArr() {
        return this.fractions;
    }
}
