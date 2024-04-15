package org.processmining.specpp.evaluation.fitness.base;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;

public interface DetailedFitnessEvaluator extends SupportsConsideredVariants {

    default DetailedFitnessEvaluation detailedEval(Place place) {
        return detailedComputation(place, getConsideredVariants());
    }

    default DetailedFitnessEvaluation subsetDetailedEval(EvaluationParameterTuple2<Place, BitMask> tuple) {
        return detailedComputation(tuple.getT1(), tuple.getT2());
    }

    DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants);


}
