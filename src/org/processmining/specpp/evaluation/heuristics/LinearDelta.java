package org.processmining.specpp.evaluation.heuristics;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.config.parameters.DeltaParameters;
import org.processmining.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Transition;

public class LinearDelta extends AbstractDeltaAdaptationFunction {
    public static class Builder extends AbstractDeltaAdaptationFunction.Builder {

        protected DelegatingDataSource<PlaceGeneratorParameters> placeGeneratorParameters = new DelegatingDataSource<>();
        protected DelegatingDataSource<IntEncodings<Transition>> transitionEncodings = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(DataRequirements.ENC_TRANS, transitionEncodings)
                                   .require(ParameterRequirements.PLACE_GENERATOR_PARAMETERS, placeGeneratorParameters);
        }

        @Override
        protected Provider buildIfFullySatisfied() {
            PlaceGeneratorParameters generatorParameters = placeGeneratorParameters.getData();
            IntEncodings<Transition> intEncodings = transitionEncodings.getData();
            int configuredMaxDepth = generatorParameters.getMaxTreeDepth();
            int naturalMaxDepth = intEncodings.getPresetEncoding().size() + intEncodings.getPostsetEncoding().size();
            return new Provider(delta.getData(), Math.min(configuredMaxDepth, naturalMaxDepth));
        }
    }

    public static class Provider extends AbstractDeltaAdaptationFunction.Provider {

        private final int maxDepth;

        public Provider(DeltaParameters deltaParameters, int maxDepth) {
            super(deltaParameters);
            this.maxDepth = maxDepth;
        }

        @Override
        protected AbstractDeltaAdaptationFunction createInstance(DeltaParameters deltaParameters) {
            return new LinearDelta(deltaParameters.getDelta(), deltaParameters.getSteepness(), maxDepth);
        }

    }

    private final double s, d_max;

    public LinearDelta(double delta, double s, double d_max) {
        super(delta);
        this.s = s;
        this.d_max = d_max;
    }

    protected double calculateModifier(int place_depth, int d) {
        return s / place_depth * (d - place_depth) / (d_max - 2);
    }

    @Override
    public String toString() {
        return "LinearDelta{" +
                "s=" + s +
                ", d_max=" + d_max +
                ", delta=" + delta +
                '}';
    }
}
