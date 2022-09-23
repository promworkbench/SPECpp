package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.base.CandidateEvaluation;
import org.processmining.specpp.datastructures.util.DisjointMergeable;
import org.processmining.specpp.datastructures.util.EnumCounts;
import org.processmining.specpp.datastructures.util.EnumFractions;

public class BasicFitnessEvaluation extends EnumFractions<BasicFitnessStatus> implements CandidateEvaluation, DisjointMergeable<BasicFitnessEvaluation> {

    private final double weight;

    public BasicFitnessEvaluation(double weight, double[] fractions) {
        super(fractions);
        this.weight = weight;
    }

    public static BasicFitnessEvaluation ofCounts(EnumCounts<BasicFitnessStatus> enumCounts) {
        double total = enumCounts.getCount(BasicFitnessStatus.UNACTIVATED) + enumCounts.getCount(BasicFitnessStatus.ACTIVATED);
        double[] fractions = new double[BasicFitnessStatus.values().length];
        for (int i = 0; i < enumCounts.counts.length; i++) {
            fractions[i] = enumCounts.counts[i] / total;
        }
        return new BasicFitnessEvaluation(total, fractions);
    }

    public double getFittingFraction() {
        return getFraction(BasicFitnessStatus.FITTING);
    }

    public double getRelativeFittingFraction() {
        return (getFraction(BasicFitnessStatus.FITTING) - getFraction(BasicFitnessStatus.UNACTIVATED)) / getFraction(BasicFitnessStatus.ACTIVATED);
    }

    public double getUnderfedFraction() {
        return getFraction(BasicFitnessStatus.UNDERFED);
    }

    public double getRelativeUnderfedFraction() {
        return getFraction(BasicFitnessStatus.UNDERFED) / getFraction(BasicFitnessStatus.ACTIVATED);
    }

    public double getOverfedFraction() {
        return getFraction(BasicFitnessStatus.OVERFED);
    }

    public double getRelativeOverfedFraction() {
        return getFraction(BasicFitnessStatus.OVERFED) / getFraction(BasicFitnessStatus.ACTIVATED);
    }

    @Override
    public void disjointMerge(BasicFitnessEvaluation other) {
        double oWeight = other.weight;
        double s = weight + oWeight;
        for (int i = 0; i < fractions.length; i++) {
            fractions[i] = weight / s * fractions[i] + oWeight / s * other.fractions[i];
        }
    }
}
