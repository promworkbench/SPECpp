package org.processmining.specpp.base;

import org.processmining.specpp.componenting.evaluation.FulfilledEvaluatorRequirement;

public interface ExaminingComposition<C extends Candidate, E extends CandidateEvaluation> extends Composition<C> {

    Evaluator<C, E> getExaminationFunction();

    FulfilledEvaluatorRequirement<C, E> getExaminingEvaluator();

}
