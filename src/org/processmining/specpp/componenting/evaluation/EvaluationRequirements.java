package org.processmining.specpp.componenting.evaluation;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.base.Evaluation;
import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.FittingVariantsEvaluation;
import org.processmining.specpp.evaluation.heuristics.AdaptedDelta;
import org.processmining.specpp.evaluation.heuristics.CandidateScore;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.specpp.util.JavaTypingUtils;

public class EvaluationRequirements {

    public static final EvaluatorRequirement<Place, BasicFitnessEvaluation> BASIC_FITNESS = evaluator(Place.class, BasicFitnessEvaluation.class);
    public static final EvaluatorRequirement<Place, DetailedFitnessEvaluation> DETAILED_FITNESS = evaluator(Place.class, DetailedFitnessEvaluation.class);
    public static final EvaluatorRequirement<Place, ComprehensiveFitnessEvaluation> COMPREHENSIVE_FITNESS = evaluator(Place.class, ComprehensiveFitnessEvaluation.class);
    public static final EvaluatorRequirement<Place, FittingVariantsEvaluation> PLACE_FITTING_VARIANTS = evaluator(Place.class, FittingVariantsEvaluation.class);

    public static final EvaluatorRequirement<EvaluationParameterTuple2<Place, BitMask>, BasicFitnessEvaluation> SUBSET_BASIC_FITNESS = evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), BasicFitnessEvaluation.class);
    public static final EvaluatorRequirement<Place, ImplicitnessRating> PLACE_IMPLICITNESS = evaluator(Place.class, ImplicitnessRating.class);
    public static final EvaluatorRequirement<Place, VariantMarkingHistories> PLACE_MARKING_HISTORY = evaluator(Place.class, VariantMarkingHistories.class);
    public static final EvaluatorRequirement<EvaluationParameterTuple2<Place, BitMask>, VariantMarkingHistories> PLACE_SUBSET_MARKING_HISTORY = evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), VariantMarkingHistories.class);
    public static final EvaluatorRequirement<EvaluationParameterTuple2<Place, Integer>, AdaptedDelta> DELTA_ADAPTATION_FUNCTION = evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), AdaptedDelta.class);
    public static final EvaluatorRequirement<Place, CandidateScore> POSTPONED_CANDIDATES_HEURISTIC = evaluator(Place.class, CandidateScore.class);


    public static <I extends Evaluable, E extends Evaluation> EvaluatorRequirement<I, E> evaluator(Class<I> evaluableClass, Class<E> evaluationClass) {
        return new EvaluatorRequirement<>(evaluableClass, evaluationClass);
    }

    public static <I extends Evaluable, E extends Evaluation> FulfilledEvaluatorRequirement<I, E> evaluator(Class<I> evaluableClass, Class<E> evaluationClass, Evaluator<I, E> evaluator) {
        return evaluator(evaluableClass, evaluationClass).fulfilWith(evaluator);
    }

    public static <I extends Evaluable, E extends Evaluation> FulfilledEvaluatorRequirement<I, E> evaluator(EvaluatorRequirement<I, E> requirement, Evaluator<I, E> evaluator) {
        return requirement.fulfilWith(evaluator);
    }

}
