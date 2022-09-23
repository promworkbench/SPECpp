package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.config.parameters.TauFitnessThresholds;

public class FitnessThresholder {

    public static boolean isTauFitting(BasicFitnessEvaluation evaluation, TauFitnessThresholds thresholds) {
        return evaluation.getFittingFraction() >= thresholds.getFittingThreshold();
    }

    public static boolean isUnderfed(BasicFitnessEvaluation evaluation, TauFitnessThresholds thresholds) {
        return evaluation.getUnderfedFraction() > thresholds.getUnderfedThreshold();
    }

    public static boolean isOverfed(BasicFitnessEvaluation evaluation, TauFitnessThresholds thresholds) {
        return evaluation.getOverfedFraction() > thresholds.getOverfedThreshold();
    }

}
