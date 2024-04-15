package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.arraybacked.EnumCounts;
import org.processmining.specpp.datastructures.util.arraybacked.EnumMapping;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;

public class ComprehensiveFitnessEvaluator extends AbstractComprehensiveFitnessEvaluator {

    public static class Builder extends AbstractFullFitnessEvaluator.Builder {

        @Override
        protected ComprehensiveFitnessEvaluator buildIfFullySatisfied() {
            return new ComprehensiveFitnessEvaluator(multiEncodedLogSource.getData(), variantSubsetSource.getDelegate(), replayComputationParametersSource.getData());
        }
    }

    public ComprehensiveFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource, ReplayComputationParameters replayComputationParameters) {
        super(multiEncodedLog, variantSubsetSource, replayComputationParameters);
    }

    public ComprehensiveFitnessEvaluation comprehensiveComputation(Place place, BitMask consideredVariants) {
        BitMask[] bitMasks = ReplayUtils.createBitMaskArray();
        int[] counts = ReplayUtils.createCountArray();
        ResultUpdater upd = (idx, f, activated, wentUnder, wentOver, notZeroAtEnd) -> {
            ReplayUtils.updateCounts(counts, f, activated, wentUnder, wentOver, notZeroAtEnd);
            ReplayUtils.updateOutcomeBitMasks(bitMasks, idx, activated, wentUnder, wentOver, notZeroAtEnd);
        };

        getVariantFrequencies();

        run(consideredVariants, place, upd);

        EnumMapping<ReplayOutcome, BitMask> replayOutcomes = new EnumMapping<>(bitMasks);
        EnumCounts<ReplayOutcome> enumCounts = new EnumCounts<>(counts);

        BasicFitnessEvaluation basicFitnessEvaluation = ReplayUtils.summarizeReplayOutcomeCounts(enumCounts);
        return new ComprehensiveFitnessEvaluation(replayOutcomes, basicFitnessEvaluation);
    }


}
