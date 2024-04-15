package org.processmining.specpp.evaluation.fitness.results;

import org.processmining.specpp.base.CandidateEvaluation;
import org.processmining.specpp.datastructures.util.DisjointMergeable;
import org.processmining.specpp.datastructures.util.arraybacked.EnumCounts;
import org.processmining.specpp.datastructures.util.arraybacked.EnumFractions;
import org.processmining.specpp.evaluation.fitness.base.BasicFitnessStatus;

public class BasicFitnessEvaluation extends EnumFractions<BasicFitnessStatus> implements CandidateEvaluation, DisjointMergeable<BasicFitnessEvaluation> {

    private final double weight;

    public BasicFitnessEvaluation(double weight, EnumFractions<BasicFitnessStatus> fractions) {
        super(fractions.getUnderlyingArr());
        this.weight = weight;
    }

    public static BasicFitnessEvaluation ofCounts(EnumCounts<BasicFitnessStatus> enumCounts) {
        double total = enumCounts.getCount(BasicFitnessStatus.NOT_ACTIVATED) + enumCounts.getCount(BasicFitnessStatus.ACTIVATED);
        double[] fracArr = new double[BasicFitnessStatus.values().length];
        for (int i = 0; i < enumCounts.counts.length; i++) {
            fracArr[i] = enumCounts.counts[i] / total;
        }
        return new BasicFitnessEvaluation(total, new EnumFractions<>(fracArr));
    }

    public double getFittingFraction() {
        return getFraction(BasicFitnessStatus.FITTING);
    }

    public double getRelativeFittingFraction() {
        return (getFraction(BasicFitnessStatus.FITTING) - getFraction(BasicFitnessStatus.NOT_ACTIVATED)) / getFraction(BasicFitnessStatus.ACTIVATED);
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
