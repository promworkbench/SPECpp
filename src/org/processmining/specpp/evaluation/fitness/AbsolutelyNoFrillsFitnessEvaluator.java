package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EnumCounts;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.vectorization.IntVector;

import java.nio.IntBuffer;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;

@SuppressWarnings("duplication")
public class AbsolutelyNoFrillsFitnessEvaluator extends AbstractBasicFitnessEvaluator {

    public AbsolutelyNoFrillsFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource, ReplayComputationParameters replayComputationParameters) {
        super(multiEncodedLog, variantSubsetSource, replayComputationParameters);
    }

    public static class Builder extends AbstractBasicFitnessEvaluator.Builder {

        @Override
        protected AbstractBasicFitnessEvaluator buildIfFullySatisfied() {
            return new AbsolutelyNoFrillsFitnessEvaluator(multiEncodedLogSource.getData(), variantSubsetSource.getDelegate(), replayComputationParametersSource.getData());
        }
    }

    @Override
    public BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        int[] counts = ReplayUtils.getCountArray();
        ResultUpdater upd = (idx, c, activated, wentUnder, wentOver, notZeroAtEnd) -> ReplayUtils.updateCounts(counts, c, activated, wentUnder, wentOver, notZeroAtEnd);

        run(consideredVariants, place, upd);

        EnumCounts<ReplayUtils.ReplayOutcomes> enumCounts = new EnumCounts<>(counts);
        return ReplayUtils.summarizeReplayOutcomeCounts(enumCounts);
    }

    @Override
    public DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        BitMask bm = new BitMask();
        int[] counts = ReplayUtils.getCountArray();
        ResultUpdater upd = (idx, c, activated, wentUnder, wentOver, notZeroAtEnd) -> {
            ReplayUtils.updateFittingVariantMask(bm, wentUnder, wentOver, notZeroAtEnd, idx);
            ReplayUtils.updateCounts(counts, c, activated, wentUnder, wentOver, notZeroAtEnd);
        };

        run(consideredVariants, place, upd);

        EnumCounts<ReplayUtils.ReplayOutcomes> enumCounts = new EnumCounts<>(counts);
        BasicFitnessEvaluation evaluation = ReplayUtils.summarizeReplayOutcomeCounts(enumCounts);
        return new DetailedFitnessEvaluation(bm, evaluation);
    }

    private void run(BitMask consideredVariants, Place place, ResultUpdater upd) {
        Spliterator<IndexedItem<Pair<IntBuffer>>> spliterator = getIndexedItemSpliterator();
        spliterator.forEachRemaining(createLambda(consideredVariants, place, upd, replayComputationParameters));
    }

    private Consumer<IndexedItem<Pair<IntBuffer>>> createLambda(BitMask consideredVariants, Place place, ResultUpdater upd, ReplayComputationParameters parameters) {
        IntUnaryOperator presetIndicator = ReplayUtils.presetIndicator(place);
        IntUnaryOperator postsetIndicator = ReplayUtils.postsetIndicator(place);
        IntVector frequencies = getVariantFrequencies();
        boolean clipMarkingAtZero = parameters.isClipMarkingAtZero();
        return ii -> {
            if (consideredVariants == null || consideredVariants.get(ii.getIndex())) {
                Pair<IntBuffer> pair = ii.getItem();
                IntBuffer presetEncodedVariant = pair.first(), postsetEncodedVariant = pair.second();
                int acc = 0;
                boolean wentUnder = false, wentOver = false, activated = false;
                while (presetEncodedVariant.hasRemaining() && postsetEncodedVariant.hasRemaining()) {
                    int i = postsetIndicator.applyAsInt(postsetEncodedVariant.get());
                    acc += i;
                    wentUnder |= acc < 0;
                    activated |= acc != 0;
                    if (clipMarkingAtZero) acc = Math.max(0, acc);
                    int j = presetIndicator.applyAsInt(presetEncodedVariant.get());
                    acc += j;
                    wentOver |= acc > 1;
                    activated |= acc != 0;
                }
                boolean notZeroAtEnd = acc > 0;
                int idx = ii.getIndex();
                int c = frequencies.get(idx);
                upd.update(idx, c, activated, wentUnder, wentOver, notZeroAtEnd);
            }
        };
    }

    @FunctionalInterface
    private interface ResultUpdater {

        void update(int variantIndex, int variantFrequency, boolean activated, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd);

    }

    @Override
    public String toString() {
        return "AbsolutelyNoFrillsEvaluator()";
    }
}
