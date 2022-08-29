package org.processmining.specpp.config.parameters;

public class DeltaParameters implements Parameters {

    private final double delta;

    public DeltaParameters(double delta) {
        this.delta = delta;
    }

    public static DeltaParameters delta(double d) {
        return new DeltaParameters(d);
    }

    public static DeltaParameters getDefault() {
        return new DeltaParameters(1);
    }

    public double getDelta() {
        return delta;
    }

    @Override
    public String toString() {
        return "DeltaParameters(" + "\uD835\uDEFF=" + delta + ")";
    }

}
