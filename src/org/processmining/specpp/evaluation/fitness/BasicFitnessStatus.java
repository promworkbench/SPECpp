package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.base.CandidateEvaluation;

public enum BasicFitnessStatus implements CandidateEvaluation {
    FITTING, UNDERFED, OVERFED, ACTIVATED, UNACTIVATED
}
