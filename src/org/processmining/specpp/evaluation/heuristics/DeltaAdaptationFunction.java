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
import org.processmining.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;

public class DeltaAdaptationFunction implements Evaluator<EvaluationParameterTuple2<Place, Integer>, DoubleScore> {

    public static class Builder extends ComponentSystemAwareBuilder<DeltaAdaptationFunction.Provider> {

        private final DelegatingDataSource<DeltaParameters> delta = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(ParameterRequirements.DELTA_PARAMETERS, delta);
        }

        @Override
        protected DeltaAdaptationFunction.Provider buildIfFullySatisfied() {
            return new DeltaAdaptationFunction.Provider(delta.getData());
        }
    }

    public static class Provider extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators {

        public Provider(DeltaParameters deltaParameters) {
            DeltaAdaptationFunction func = new DeltaAdaptationFunction(deltaParameters.getDelta());
            globalComponentSystem().provide(EvaluationRequirements.DELTA_ADAPTATION_FUNCTION.fulfilWith(func));
        }
    }

    private final double delta;
    private final DoubleScore base;

    public DeltaAdaptationFunction(double delta) {
        this.delta = delta;
        base = new DoubleScore(delta);
    }


    @Override
    public DoubleScore eval(EvaluationParameterTuple2<Place, Integer> input) {
        Place place = input.getT1();
        Integer treeLevel = input.getT2();
        if (place.size() == treeLevel) return new DoubleScore(0);
        else return base;
    }
}
