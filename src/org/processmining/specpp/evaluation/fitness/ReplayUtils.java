package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.arraybacked.EnumCounts;
import org.processmining.specpp.datastructures.util.arraybacked.EnumFractions;
import org.processmining.specpp.datastructures.util.arraybacked.EnumMapping;
import org.processmining.specpp.evaluation.fitness.base.BasicFitnessStatus;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;

import java.nio.IntBuffer;
import java.util.EnumSet;
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

    public static int[] createCountArray() {
        return new int[ReplayOutcome.values().length];
    }


    public static BitMask[] createBitMaskArray() {
        BitMask[] bms = new BitMask[ReplayOutcome.values().length];
        for (int i = 0; i < bms.length; i++) {
            bms[i] = new BitMask();
        }
        return bms;
    }

    public static void updateCounts(int[] counts, int count, boolean activated, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd) {
        if (!activated) {
            counts[ReplayOutcome.NOT_ACTIVATED.ordinal()] += count;
            counts[ReplayOutcome.FITTING.ordinal()] += count;
        } else {
            counts[ReplayOutcome.ACTIVATED.ordinal()] += count;
            if (!wentUnder && !notZeroAtEnd) counts[ReplayOutcome.FITTING.ordinal()] += count;
            if (wentOver) counts[ReplayOutcome.UNSAFE.ordinal()] += count;
            if (wentUnder) counts[ReplayOutcome.UNDERFED.ordinal()] += count;
            if (notZeroAtEnd) counts[ReplayOutcome.OVERFED.ordinal()] += count;
        }
    }

    public static void updateOutcomeBitMasks(BitMask[] bitMasks, int idx, boolean activated, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd) {
        if (!activated) {
            bitMasks[ReplayOutcome.NOT_ACTIVATED.ordinal()].set(idx);
            bitMasks[ReplayOutcome.FITTING.ordinal()].set(idx);
        } else {
            bitMasks[ReplayOutcome.ACTIVATED.ordinal()].set(idx);
            if (!wentUnder && !notZeroAtEnd) bitMasks[ReplayOutcome.FITTING.ordinal()].set(idx);
            if (wentOver) bitMasks[ReplayOutcome.UNSAFE.ordinal()].set(idx);
            if (wentUnder) bitMasks[ReplayOutcome.UNDERFED.ordinal()].set(idx);
            if (notZeroAtEnd) bitMasks[ReplayOutcome.OVERFED.ordinal()].set(idx);
        }
    }

    public static void updateFittingVariantMask(BitMask bm, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd, int idx) {
        if (!wentUnder && !notZeroAtEnd) bm.set(idx);
    }

    public static EnumSet<ReplayOutcome> createReplayOutcomeEnumSet(boolean activated, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd) {
        if (!activated) return EnumSet.of(ReplayOutcome.NOT_ACTIVATED, ReplayOutcome.FITTING);
        else {
            EnumSet<ReplayOutcome> enumSet = EnumSet.of(ReplayOutcome.ACTIVATED);
            if (!wentUnder && !notZeroAtEnd) enumSet.add(ReplayOutcome.FITTING);
            if (wentUnder) enumSet.add(ReplayOutcome.UNDERFED);
            if (wentOver) enumSet.add(ReplayOutcome.UNSAFE);
            if (notZeroAtEnd) enumSet.add(ReplayOutcome.OVERFED);
            return enumSet;
        }
    }

    static EnumSet<ReplayOutcome> variantReplay(IntBuffer presetVariantBuffer, IntUnaryOperator presetIndicator, IntBuffer postsetVariantBuffer, IntUnaryOperator postsetIndicator) {
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
        return createReplayOutcomeEnumSet(activated, wentUnder, wentOver, notEndingOnZero);
    }

    public static EnumSet<ReplayOutcome> markingBasedReplay(IntBuffer markingHistory) {
        int next = 0;
        boolean wentUnder = false, activated = false, wentOver = false;
        while (markingHistory.hasRemaining()) {
            next = markingHistory.get();
            activated |= next != 0;
            wentUnder |= next < 0;
            wentOver |= next > 1;
        }
        return createReplayOutcomeEnumSet(activated, wentUnder, wentOver, next > 0);
    }

    public static BasicFitnessEvaluation summarizeReplayOutcomeCounts(EnumCounts<ReplayOutcome> enumCounts) {
        int activated = enumCounts.getCount(ReplayOutcome.ACTIVATED);
        int unactivated = enumCounts.getCount(ReplayOutcome.NOT_ACTIVATED);
        double total = activated + unactivated;
        double[] fracArr = new double[BasicFitnessStatus.values().length];
        fracArr[BasicFitnessStatus.FITTING.ordinal()] = enumCounts.getCount(ReplayOutcome.FITTING) / total;
        fracArr[BasicFitnessStatus.UNDERFED.ordinal()] = enumCounts.getCount(ReplayOutcome.UNDERFED) / total;
        fracArr[BasicFitnessStatus.OVERFED.ordinal()] = enumCounts.getCount(ReplayOutcome.OVERFED) / total;
        fracArr[BasicFitnessStatus.ACTIVATED.ordinal()] = activated / total;
        fracArr[BasicFitnessStatus.NOT_ACTIVATED.ordinal()] = unactivated / total;
        return new BasicFitnessEvaluation(total, new EnumFractions<>(fracArr));
    }

    public static EnumCounts<ReplayOutcome> aggregateReplayOutcomeBitMasks(EnumMapping<ReplayOutcome, BitMask> replayOutcomes, IntUnaryOperator variantFrequencyGetter) {
        EnumCounts<ReplayOutcome> enumCounts = new EnumCounts<>(createCountArray());
        for (ReplayOutcome outcome : ReplayOutcome.values()) {
            enumCounts.setCount(outcome, replayOutcomes.get(outcome).stream().map(variantFrequencyGetter).sum());
        }
        return enumCounts;
    }
}
