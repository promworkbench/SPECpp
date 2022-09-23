package org.processmining.specpp.evaluation.heuristics;

import org.processmining.specpp.config.parameters.DeltaParameters;

public class NoDelta extends AbstractDeltaAdaptationFunction {

    public static class Builder extends AbstractDeltaAdaptationFunction.Builder {
        @Override
        protected AbstractDeltaAdaptationFunction.Provider buildIfFullySatisfied() {
            return new Provider(delta.getData());
        }
    }

    public static class Provider extends AbstractDeltaAdaptationFunction.Provider {

        public Provider(DeltaParameters deltaParameters) {
            super(deltaParameters);
        }

        @Override
        protected AbstractDeltaAdaptationFunction createInstance(DeltaParameters deltaParameters) {
            return new NoDelta();
        }
    }

    public NoDelta() {
        super(1);
    }


    @Override
    protected double calculateModifier(int place_depth, int d) {
        return 1;
    }

    @Override
    public String toString() {
        return "NoDelta()";
    }
}
