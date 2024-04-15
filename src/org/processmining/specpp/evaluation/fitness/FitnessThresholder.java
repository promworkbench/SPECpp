package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;

public class FitnessThresholder {

    public static boolean isTauFitting(double fitness, TauFitnessThresholds thresholds) {
        return fitness >= thresholds.getFittingThreshold();
    }

    public static boolean isUnderfed(double underfed, TauFitnessThresholds thresholds) {
        return underfed > thresholds.getUnderfedThreshold();
    }

    public static boolean isOverfed(double overfed, TauFitnessThresholds thresholds) {
        return overfed > thresholds.getOverfedThreshold();
    }

    public static boolean isTauFitting(BasicFitnessEvaluation evaluation, TauFitnessThresholds thresholds) {
        return isTauFitting(evaluation.getFittingFraction(), thresholds);
    }

    public static boolean isUnderfed(BasicFitnessEvaluation evaluation, TauFitnessThresholds thresholds) {
        return isUnderfed(evaluation.getUnderfedFraction(), thresholds);
    }

    public static boolean isOverfed(BasicFitnessEvaluation evaluation, TauFitnessThresholds thresholds) {
        return isOverfed(evaluation.getOverfedFraction(), thresholds);
    }

}
