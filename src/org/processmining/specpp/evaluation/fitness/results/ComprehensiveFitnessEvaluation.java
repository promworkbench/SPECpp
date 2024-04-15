package org.processmining.specpp.evaluation.fitness.results;

import org.processmining.specpp.base.CandidateEvaluation;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.util.DisjointMergeable;
import org.processmining.specpp.datastructures.util.arraybacked.EnumMapping;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;

public class ComprehensiveFitnessEvaluation implements CandidateEvaluation, DisjointMergeable<ComprehensiveFitnessEvaluation>, FittingVariantsEvaluation {


    private final BasicFitnessEvaluation fractions;
    private final EnumMapping<ReplayOutcome, BitMask> replayOutcomes;

    public ComprehensiveFitnessEvaluation(EnumMapping<ReplayOutcome, BitMask> replayOutcomes, BasicFitnessEvaluation basicFitnessEvaluation) {
        this.replayOutcomes = replayOutcomes;
        this.fractions = basicFitnessEvaluation;
    }

    public EnumMapping<ReplayOutcome, BitMask> getReplayOutcomes() {
        return replayOutcomes;
    }

    public BasicFitnessEvaluation getFractionalEvaluation() {
        return fractions;
    }

    @Override
    public void disjointMerge(ComprehensiveFitnessEvaluation other) {
        fractions.disjointMerge(other.fractions);
        EnumMapping<ReplayOutcome, BitMask> otherReplayOutcomes = other.getReplayOutcomes();
        for (ReplayOutcome outcome : ReplayOutcome.values()) {
            replayOutcomes.get(outcome).or(otherReplayOutcomes.get(outcome));
        }
    }

    public BitMask getFittingVariants() {
        return getReplayOutcomeBitMask(ReplayOutcome.FITTING);
    }

    public BitMask getReplayOutcomeBitMask(ReplayOutcome outcome) {
        return replayOutcomes.get(outcome);
    }

}
