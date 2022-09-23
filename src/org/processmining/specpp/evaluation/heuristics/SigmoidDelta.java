package org.processmining.specpp.evaluation.heuristics;

import org.processmining.specpp.config.parameters.DeltaParameters;

public class SigmoidDelta extends AbstractDeltaAdaptationFunction {


    public static class Builder extends AbstractDeltaAdaptationFunction.Builder {

        @Override
        protected Provider buildIfFullySatisfied() {
            return new Provider(delta.getData());
        }
    }

    public static class Provider extends AbstractDeltaAdaptationFunction.Provider {

        public Provider(DeltaParameters deltaParameters) {
            super(deltaParameters);
        }

        @Override
        protected AbstractDeltaAdaptationFunction createInstance(DeltaParameters deltaParameters) {
            return new SigmoidDelta(deltaParameters.getDelta(), deltaParameters.getSteepness());
        }
    }

    protected double s;

    public SigmoidDelta(double delta, double s) {
        super(delta);
        this.s = s;
    }

    @Override
    protected double calculateModifier(int place_depth, int d) {
        return 2 / (1 + Math.exp(-s / place_depth * (d - place_depth))) - 1;
    }

    @Override
    public String toString() {
        return "SigmoidDelta{" +
                "s=" + s +
                ", delta=" + delta +
                '}';
    }
}
