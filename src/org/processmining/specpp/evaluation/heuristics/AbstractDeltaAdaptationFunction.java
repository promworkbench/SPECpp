package org.processmining.specpp.evaluation.heuristics;

import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.config.parameters.DeltaParameters;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;

public abstract class AbstractDeltaAdaptationFunction implements Evaluator<EvaluationParameterTuple2<Place, Integer>, AdaptedDelta> {

    public static abstract class Builder extends ComponentSystemAwareBuilder<AbstractDeltaAdaptationFunction.Provider> {

        protected final DelegatingDataSource<DeltaParameters> delta = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(ParameterRequirements.DELTA_PARAMETERS, delta);
        }

    }

    public static abstract class Provider extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators {

        public Provider(DeltaParameters deltaParameters) {
            globalComponentSystem().provide(EvaluationRequirements.DELTA_ADAPTATION_FUNCTION.fulfilWith(createInstance(deltaParameters)));
        }

        protected abstract AbstractDeltaAdaptationFunction createInstance(DeltaParameters deltaParameters);

    }

    protected double delta;

    public AbstractDeltaAdaptationFunction(double delta) {
        this.delta = delta;
    }

    @Override
    public AdaptedDelta eval(EvaluationParameterTuple2<Place, Integer> input) {
        Place place = input.getT1();
        int tree_depth = input.getT2();
        int place_depth = place.size();
        return new AdaptedDelta(delta * calculateModifier(place_depth, tree_depth));
    }

    protected abstract double calculateModifier(int place_depth, int d);

}
