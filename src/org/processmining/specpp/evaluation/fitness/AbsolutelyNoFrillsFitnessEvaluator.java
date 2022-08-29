package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EnumCounts;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.datastructures.vectorization.IntVector;

import java.nio.IntBuffer;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

@SuppressWarnings("duplication")
public class AbsolutelyNoFrillsFitnessEvaluator extends AbstractBasicFitnessEvaluator {

    @Override
    public BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(AbstractBasicFitnessEvaluator.BASIC_EVALUATION);
        int[] counts = ReplayUtils.getCountArray();
        ResultUpdater upd = (idx, c, activated, wentUnder, wentOver, notZeroAtEnd) -> ReplayUtils.updateCounts(counts, c, activated, wentUnder, wentOver, notZeroAtEnd);

        run(consideredVariants, place, upd);

        EnumCounts<ReplayUtils.ReplayOutcomes> enumCounts = new EnumCounts<>(counts);
        BasicFitnessEvaluation evaluation = ReplayUtils.summarizeReplayOutcomeCounts(enumCounts);
        timeStopper.stop(AbstractBasicFitnessEvaluator.BASIC_EVALUATION);
        return evaluation;
    }

    @Override
    public DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(AbstractBasicFitnessEvaluator.DETAILED_EVALUATION);

        BitMask bm = new BitMask();
        int[] counts = ReplayUtils.getCountArray();
        ResultUpdater upd = (idx, c, activated, wentUnder, wentOver, notZeroAtEnd) -> {
            ReplayUtils.updateFittingVariantMask(bm, wentUnder, wentOver, notZeroAtEnd, idx);
            ReplayUtils.updateCounts(counts, c, activated, wentUnder, wentOver, notZeroAtEnd);
        };

        run(consideredVariants, place, upd);

        EnumCounts<ReplayUtils.ReplayOutcomes> enumCounts = new EnumCounts<>(counts);
        BasicFitnessEvaluation evaluation = ReplayUtils.summarizeReplayOutcomeCounts(enumCounts);
        DetailedFitnessEvaluation res = new DetailedFitnessEvaluation(bm, evaluation);
        timeStopper.stop(AbstractBasicFitnessEvaluator.DETAILED_EVALUATION);
        return res;
    }

    private void run(BitMask consideredVariants, Place place, ResultUpdater upd) {
        IntUnaryOperator presetIndicator = ReplayUtils.presetIndicator(place);
        IntUnaryOperator postsetIndicator = ReplayUtils.postsetIndicator(place);
        Spliterator<IndexedItem<Tuple2<IntBuffer, IntBuffer>>> spliterator = getIndexedItemSpliterator();
        IntVector frequencies = getVariantFrequencies();
        spliterator.forEachRemaining(ii -> {
            if (consideredVariants == null || consideredVariants.get(ii.getIndex())) {
                Tuple2<IntBuffer, IntBuffer> t = ii.getItem();
                IntBuffer presetEncodedVariant = t.getT1(), postsetEncodedVariant = t.getT2();
                int acc = 0;
                boolean wentUnder = false, wentOver = false, activated = false;
                while (presetEncodedVariant.hasRemaining() && postsetEncodedVariant.hasRemaining()) {
                    int i = postsetIndicator.applyAsInt(postsetEncodedVariant.get());
                    acc += i;
                    wentUnder |= acc < 0;
                    activated |= acc != 0;
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
        });
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
