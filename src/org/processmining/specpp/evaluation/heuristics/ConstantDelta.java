package org.processmining.specpp.evaluation.heuristics;

import org.processmining.specpp.config.parameters.DeltaParameters;

public class ConstantDelta extends AbstractDeltaAdaptationFunction {

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
            return new ConstantDelta(deltaParameters.getDelta());
        }
    }

    public ConstantDelta(double delta) {
        super(delta);
    }

    @Override
    protected double calculateModifier(int place_depth, int d) {
        return 1;
    }

    @Override
    public String toString() {
        return "ConstantDelta{" +
                "delta=" + delta +
                '}';
    }
}
