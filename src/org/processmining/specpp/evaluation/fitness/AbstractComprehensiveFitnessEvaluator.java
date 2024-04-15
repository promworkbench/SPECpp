package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;
import org.processmining.specpp.util.JavaTypingUtils;

public abstract class AbstractComprehensiveFitnessEvaluator extends AbstractFitnessEvaluator {
    public AbstractComprehensiveFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource, ReplayComputationParameters replayComputationParameters) {
        super(multiEncodedLog, variantSubsetSource, replayComputationParameters);
        globalComponentSystem().provide(EvaluationRequirements.evaluator(Place.class, ComprehensiveFitnessEvaluation.class, this::comprehensiveEval))
                .provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), ComprehensiveFitnessEvaluation.class, this::comprehensiveSubsetEval));

    }

    public ComprehensiveFitnessEvaluation comprehensiveSubsetEval(EvaluationParameterTuple2<Place, BitMask> tuple) {
        return comprehensiveComputation(tuple.getT1(), tuple.getT2());
    }

    public ComprehensiveFitnessEvaluation comprehensiveEval(Place place) {
        return comprehensiveComputation(place, getConsideredVariants());
    }

    public abstract ComprehensiveFitnessEvaluation comprehensiveComputation(Place place, BitMask consideredVariants);

    //    protected BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
//        return comprehensiveComputation(place, consideredVariants).getFractionalEvaluation();
//    }
//
//    protected DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
//        ComprehensiveFitnessEvaluation cfe = comprehensiveComputation(place, consideredVariants);
//        return new DetailedFitnessEvaluation(cfe.getFittingVariants(), cfe.getFractionalEvaluation());
//    }
}
