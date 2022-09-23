package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.EnumCounts;
import org.processmining.specpp.datastructures.util.IndexedItem;

import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.function.IntUnaryOperator;

public class ReplayUtils {
    // TODO efficiency improvement opportunity
    public static IntUnaryOperator presetIndicator(final Place place) {
        final BitEncodedSet<Transition> preset = place.preset();
        return i -> preset.containsIndex(i) ? 1 : 0;
    }

    public static IntUnaryOperator postsetIndicator(final Place place) {
        final BitEncodedSet<Transition> postset = place.postset();
        return i -> postset.containsIndex(i) ? -1 : 0;
    }

    public static BasicFitnessEvaluation summarizeReplayOutcomeCounts(EnumCounts<ReplayOutcomes> enumCounts) {
        int activated = enumCounts.getCount(ReplayOutcomes.ACTIVATED);
        int unactivated = enumCounts.getCount(ReplayOutcomes.NOT_ACTIVATED);
        double total = activated + unactivated;
        double[] fracArr = new double[BasicFitnessStatus.values().length];
        fracArr[BasicFitnessStatus.FITTING.ordinal()] = enumCounts.getCount(ReplayOutcomes.FITTING) / total;
        fracArr[BasicFitnessStatus.UNDERFED.ordinal()] = enumCounts.getCount(ReplayOutcomes.UNDERFED) / total;
        fracArr[BasicFitnessStatus.OVERFED.ordinal()] = enumCounts.getCount(ReplayOutcomes.OVERFED) / total;
        fracArr[BasicFitnessStatus.ACTIVATED.ordinal()] = activated / total;
        fracArr[BasicFitnessStatus.UNACTIVATED.ordinal()] = unactivated / total;
        return new BasicFitnessEvaluation(total, fracArr);
    }

    public static int[] getCountArray() {
        return new int[ReplayOutcomes.values().length];
    }

    public static void updateCounts(int[] counts, int count, boolean activated, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd) {
        if (!activated) {
            counts[ReplayOutcomes.NOT_ACTIVATED.ordinal()] += count;
            counts[ReplayOutcomes.FITTING.ordinal()] += count;
        } else {
            counts[ReplayOutcomes.ACTIVATED.ordinal()] += count;
            if (!wentUnder && !notZeroAtEnd) counts[ReplayOutcomes.FITTING.ordinal()] += count;
            if (wentOver) counts[ReplayOutcomes.UNSAFE.ordinal()] += count;
            if (wentUnder) counts[ReplayOutcomes.UNDERFED.ordinal()] += count;
            if (notZeroAtEnd) counts[ReplayOutcomes.OVERFED.ordinal()] += count;
        }
    }

    public static EnumSet<ReplayOutcomes> getReplayOutcomeEnumSet(boolean activated, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd) {
        if (!activated) return EnumSet.of(ReplayOutcomes.NOT_ACTIVATED, ReplayOutcomes.FITTING);
        else {
            EnumSet<ReplayOutcomes> enumSet = EnumSet.of(ReplayOutcomes.ACTIVATED);
            if (!wentUnder && !notZeroAtEnd) enumSet.add(ReplayOutcomes.FITTING);
            if (wentUnder) enumSet.add(ReplayOutcomes.UNDERFED);
            if (wentOver) enumSet.add(ReplayOutcomes.UNSAFE);
            if (notZeroAtEnd) enumSet.add(ReplayOutcomes.OVERFED);
            return enumSet;
        }
    }

    public static void updateFittingVariantMask(BitMask bm, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd, int idx) {
        if (!wentUnder && !notZeroAtEnd) bm.set(idx);
    }

    public static BitMask[] getBitMasks() {
        BitMask[] bms = new BitMask[ReplayOutcomes.values().length];
        for (int i = 0; i < bms.length; i++) {
            bms[i] = new BitMask();
        }
        return bms;
    }

    static EnumSet<ReplayOutcomes> variantReplay(IntBuffer presetVariantBuffer, IntUnaryOperator presetIndicator, IntBuffer postsetVariantBuffer, IntUnaryOperator postsetIndicator) {
        int acc = 0;
        boolean wentUnder = false, wentOver = false;
        boolean activated = false;
        while (presetVariantBuffer.hasRemaining() && postsetVariantBuffer.hasRemaining()) {
            int postsetExecution = postsetIndicator.applyAsInt(postsetVariantBuffer.get());
            int presetExecution = presetIndicator.applyAsInt(presetVariantBuffer.get());
            acc += postsetExecution;
            wentUnder |= acc < 0;
            activated |= acc != 0;
            acc += presetExecution;
            wentOver |= acc > 1;
            activated |= acc != 0;
        }
        boolean notEndingOnZero = acc > 0;
        return getReplayOutcomeEnumSet(activated, wentUnder, wentOver, notEndingOnZero);
    }

    public static EnumSet<ReplayUtils.ReplayOutcomes> markingBasedReplay(IntBuffer markingHistory) {
        int next = 0;
        boolean wentUnder = false, activated = false, wentOver = false;
        while (markingHistory.hasRemaining()) {
            next = markingHistory.get();
            activated |= next != 0;
            wentUnder |= next < 0;
            wentOver |= next > 1;
        }
        return getReplayOutcomeEnumSet(activated, wentUnder, wentOver, next > 0);
    }

    public static <R> R computeHere(AbstractEnumSetReplayTask<ReplayOutcomes, R> task) {
        return task.computeHere();
    }

    public static <R> R computeForkJoinLike(AbstractEnumSetReplayTask<ReplayOutcomes, R> task) {
        task.fork();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static AbstractEnumSetReplayTask<ReplayOutcomes, BasicFitnessEvaluation> createBasicReplayTask(Spliterator<IndexedItem<EnumSet<ReplayOutcomes>>> spliterator, IntUnaryOperator variantFrequencyMapper) {
        return new BasicReplayTask(spliterator, variantFrequencyMapper, ReplayOutcomes.values().length);
    }

    public static AbstractEnumSetReplayTask<ReplayOutcomes, DetailedFitnessEvaluation> createDetailedReplayTask(Spliterator<IndexedItem<EnumSet<ReplayOutcomes>>> spliterator, IntUnaryOperator variantFrequencyMapper) {
        return new DetailedReplayTask(spliterator, variantFrequencyMapper, ReplayOutcomes.values().length);
    }

    public enum ReplayOutcomes {
        FITTING, UNDERFED, OVERFED, UNSAFE, ACTIVATED, NOT_ACTIVATED
    }
}
