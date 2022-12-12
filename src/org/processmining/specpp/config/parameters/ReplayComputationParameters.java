package org.processmining.specpp.config.parameters;

public class ReplayComputationParameters implements Parameters {

    private final boolean clipMarkingAtZero;

    public ReplayComputationParameters(boolean clipMarkingAtZero) {
        this.clipMarkingAtZero = clipMarkingAtZero;
    }

    public static ReplayComputationParameters getDefault() {
        return new ReplayComputationParameters(true);
    }

    public static ReplayComputationParameters permitNegative(boolean permitNegativeMarkingsDuringReplay) {
        return new ReplayComputationParameters(!permitNegativeMarkingsDuringReplay);
    }

    public boolean isClipMarkingAtZero() {
        return clipMarkingAtZero;
    }

    @Override
    public String toString() {
        return "ReplayComputationParameters{" + "clipMarkingAtZero=" + clipMarkingAtZero + '}';
    }
}
