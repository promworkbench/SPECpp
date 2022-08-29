package org.processmining.specpp.config.parameters;

public class SimplifiedFitnessThresholds {

    private final double tau;

    public SimplifiedFitnessThresholds(double tau) {
        this.tau = tau;
    }

    public double getFittingThreshold() {
        return tau;
    }

    public double getUnderfedThreshold() {
        return 1 - tau;
    }

    public double getOverfedThreshold() {
        return 1 - tau;
    }

}
