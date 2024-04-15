package org.processmining.specpp.evaluation.fitness.results;

import org.processmining.specpp.base.CandidateEvaluation;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.util.DisjointMergeable;

public class DetailedFitnessEvaluation implements CandidateEvaluation, DisjointMergeable<DetailedFitnessEvaluation>, FittingVariantsEvaluation {

    private final BasicFitnessEvaluation fractions;
    private final BitMask fittingVariants;

    public DetailedFitnessEvaluation(BitMask fittingVariants, BasicFitnessEvaluation basicFitnessEvaluation) {
        this.fittingVariants = fittingVariants;
        this.fractions = basicFitnessEvaluation;
    }

    public BasicFitnessEvaluation getFractionalEvaluation() {
        return fractions;
    }

    @Override
    public void disjointMerge(DetailedFitnessEvaluation other) {
        fittingVariants.or(other.fittingVariants);
        fractions.disjointMerge(other.fractions);
    }

    public BitMask getFittingVariants() {
        return fittingVariants;
    }
}
