package org.processmining.specpp.evaluation.fitness.base;

import org.processmining.specpp.base.CandidateEvaluation;

public enum BasicFitnessStatus implements CandidateEvaluation {
    FITTING, UNDERFED, OVERFED, ACTIVATED, NOT_ACTIVATED
}
