package org.processmining.specpp.config.parameters;

public class TauFitnessThresholds implements Parameters {

    public static TauFitnessThresholds tau(double t) {
        return new TauFitnessThresholds(t);
    }

    public static TauFitnessThresholds getDefault() {
        return tau(1);
    }

    private final double tau;

    public TauFitnessThresholds(double tau) {
        this.tau = tau;
    }

    public double getTau() {
        return tau;
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


    @Override
    public String toString() {
        return "TauFitnessThresholds(\uD835\uDED5=" + tau + ")";
    }
}
