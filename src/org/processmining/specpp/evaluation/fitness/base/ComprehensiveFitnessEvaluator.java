package org.processmining.specpp.evaluation.fitness.base;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;

public interface ComprehensiveFitnessEvaluator extends SupportsConsideredVariants {

    default ComprehensiveFitnessEvaluation comprehensiveEval(Place place) {
        return comprehensiveComputation(place, getConsideredVariants());
    }

    default ComprehensiveFitnessEvaluation subsetComprehensiveEval(EvaluationParameterTuple2<Place, BitMask> tuple) {
        return comprehensiveComputation(tuple.getT1(), tuple.getT2());
    }

    ComprehensiveFitnessEvaluation comprehensiveComputation(Place place, BitMask consideredVariants);


}
