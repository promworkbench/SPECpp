package org.processmining.specpp.composition;

import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.evaluation.implicitness.BooleanImplicitness;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.specpp.util.JavaTypingUtils;

import java.util.Collection;

public class LightweightPlaceComposition extends BasePlaceComposition implements OffersImplicitness {
    protected DelegatingEvaluator<EvaluationParameterTuple2<Place, Collection<Place>>, BooleanImplicitness> calculator = new DelegatingEvaluator<>();

    public LightweightPlaceComposition() {
        globalComponentSystem().require(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), BooleanImplicitness.class), calculator);
        localComponentSystem().provide(EvaluationRequirements.PLACE_IMPLICITNESS.fulfilWith(this::rateImplicitness));
    }

    public ImplicitnessRating rateImplicitness(Place place) {
        return calculator.eval(new EvaluationParameterTuple2<>(place, candidates));
    }

}
