package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.evaluation.fitness.base.BasicFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.base.ComprehensiveFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.base.DetailedFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;
import org.processmining.specpp.util.JavaTypingUtils;

public abstract class AbstractFullFitnessEvaluator extends AbstractFitnessEvaluator implements BasicFitnessEvaluator, DetailedFitnessEvaluator, ComprehensiveFitnessEvaluator {


    public AbstractFullFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource, ReplayComputationParameters replayComputationParameters) {
        super(multiEncodedLog, variantSubsetSource, replayComputationParameters);
        globalComponentSystem().provide(EvaluationRequirements.evaluator(Place.class, BasicFitnessEvaluation.class, this::basicEval))
                .provide(EvaluationRequirements.evaluator(Place.class, DetailedFitnessEvaluation.class, this::detailedEval))
                .provide(EvaluationRequirements.evaluator(Place.class, ComprehensiveFitnessEvaluation.class, this::comprehensiveEval))
                .provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), BasicFitnessEvaluation.class, this::subsetBasicEval))
                .provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), DetailedFitnessEvaluation.class, this::subsetDetailedEval))
                .provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), ComprehensiveFitnessEvaluation.class, this::subsetComprehensiveEval));
    }

    public abstract BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants);

    public abstract DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants);

    public abstract ComprehensiveFitnessEvaluation comprehensiveComputation(Place place, BitMask consideredVariants);

}
