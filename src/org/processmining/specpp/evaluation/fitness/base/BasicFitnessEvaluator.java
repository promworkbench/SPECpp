package org.processmining.specpp.evaluation.fitness.base;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;

public interface BasicFitnessEvaluator extends SupportsConsideredVariants {

    default BasicFitnessEvaluation basicEval(Place place) {
        return basicComputation(place, getConsideredVariants());
    }

    default BasicFitnessEvaluation subsetBasicEval(EvaluationParameterTuple2<Place, BitMask> tuple) {
        return basicComputation(tuple.getT1(), tuple.getT2());
    }

    BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants);

}
