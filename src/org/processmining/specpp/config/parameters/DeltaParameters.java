package org.processmining.specpp.config.parameters;

public class DeltaParameters implements Parameters {

    private final double delta, steepness;

    public DeltaParameters(double delta, double steepness) {
        this.delta = delta;
        this.steepness = steepness;
    }

    public static DeltaParameters delta(double d) {
        return new DeltaParameters(d, 1);
    }

    public static DeltaParameters steepDelta(double d, double s) {
        return new DeltaParameters(d, s);
    }

    public static DeltaParameters getDefault() {
        return new DeltaParameters(1, 1);
    }

    public double getDelta() {
        return delta;
    }

    public double getSteepness() {
        return steepness;
    }

    @Override
    public String toString() {
        return "DeltaParameters(" + "\uD835\uDEFF=" + delta + ", s=" + steepness + ")";
    }
}
