package org.processmining.specpp.evaluation.markings;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IndexSubset;
import org.processmining.specpp.datastructures.log.impls.EncodedLog;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.vectorization.IVSComputations;
import org.processmining.specpp.datastructures.vectorization.IntVectorStorage;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;
import org.processmining.specpp.evaluation.fitness.ReplayUtils;

public class QuickReplay {


    public static VariantMarkingHistories makeHistoryOn(BitMask interestingVariants, MultiEncodedLog multiEncodedLog, Place place) {
        assert multiEncodedLog.variantIndices().isSupersetOf(interestingVariants);
        EncodedLog pre = multiEncodedLog.pre(), post = multiEncodedLog.post();
        IntVectorStorage interleft = IVSComputations.interleaveOn(interestingVariants, post.getEncodedVariantVectors(), ReplayUtils.postsetIndicator(place), pre.getEncodedVariantVectors(), ReplayUtils.presetIndicator(place));
        return new VariantMarkingHistories(IndexSubset.of(interestingVariants), IVSComputations.vectorwiseCumulation(interleft));
    }

    public static VariantMarkingHistories makeHistory(MultiEncodedLog data, Place input) {
        return makeHistoryOn(data.variantIndices(), data, input);
    }

}
