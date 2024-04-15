package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.arraybacked.EnumCounts;
import org.processmining.specpp.datastructures.util.arraybacked.EnumMapping;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;

@SuppressWarnings("duplication")
public class BaselineFitnessEvaluator extends AbstractFullFitnessEvaluator {

    public BaselineFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource, ReplayComputationParameters replayComputationParameters) {
        super(multiEncodedLog, variantSubsetSource, replayComputationParameters);
    }

    public static class Builder extends AbstractFullFitnessEvaluator.Builder {

        @Override
        protected AbstractFullFitnessEvaluator buildIfFullySatisfied() {
            return new BaselineFitnessEvaluator(multiEncodedLogSource.getData(), variantSubsetSource.getDelegate(), replayComputationParametersSource.getData());
        }
    }

    @Override
    public BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        int[] counts = ReplayUtils.createCountArray();
        ResultUpdater upd = (idx, c, activated, wentUnder, wentOver, notZeroAtEnd) -> ReplayUtils.updateCounts(counts, c, activated, wentUnder, wentOver, notZeroAtEnd);

        run(consideredVariants, place, upd);

        EnumCounts<ReplayOutcome> enumCounts = new EnumCounts<>(counts);
        return ReplayUtils.summarizeReplayOutcomeCounts(enumCounts);
    }

    @Override
    public DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        BitMask bm = new BitMask();
        int[] counts = ReplayUtils.createCountArray();
        ResultUpdater upd = (idx, c, activated, wentUnder, wentOver, notZeroAtEnd) -> {
            ReplayUtils.updateFittingVariantMask(bm, wentUnder, wentOver, notZeroAtEnd, idx);
            ReplayUtils.updateCounts(counts, c, activated, wentUnder, wentOver, notZeroAtEnd);
        };

        run(consideredVariants, place, upd);

        EnumCounts<ReplayOutcome> enumCounts = new EnumCounts<>(counts);
        BasicFitnessEvaluation evaluation = ReplayUtils.summarizeReplayOutcomeCounts(enumCounts);
        return new DetailedFitnessEvaluation(bm, evaluation);
    }

    @Override
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

    @Override
    public String toString() {
        return "BaselineFitnessEvaluator()";
    }
}
