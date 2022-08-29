package org.processmining.specpp.datastructures.encoding;

import java.util.function.IntToDoubleFunction;

public class WeightedBitMask extends BitMask {

    private double weight;
    private IntToDoubleFunction currentWeightFunction;

    public WeightedBitMask() {
    }

    public WeightedBitMask(double weight, IntToDoubleFunction currentWeightFunction) {
        this.weight = weight;
        this.currentWeightFunction = currentWeightFunction;
    }

    public WeightedBitMask(BitMask bitMask, IntToDoubleFunction weightFunction) {
        bitMask.stream().forEach(this::set);
        reweigh(weightFunction);
    }

    private void reweigh(IntToDoubleFunction weightFunction) {
        weight = stream().mapToDouble(weightFunction).sum();
        currentWeightFunction = weightFunction;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public void union(BitMask other) {
        super.union(other);
        reweigh(currentWeightFunction);
    }

    @Override
    public void setminus(BitMask other) {
        super.setminus(other);
        reweigh(currentWeightFunction);
    }

    @Override
    public void intersection(BitMask other) {
        super.intersection(other);
        reweigh(currentWeightFunction);
    }

    @Override
    public BitMask copy() {
        BitMask result = new WeightedBitMask(weight, currentWeightFunction);
        stream().forEach(result::set);
        return result;
    }

    @Override
    public String toString() {
        return "WeightedBitMask{" +
                "weight=" + weight +
                ", mask=" + super.toString() +
                '}';
    }
}
