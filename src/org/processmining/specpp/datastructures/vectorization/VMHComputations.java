package org.processmining.specpp.datastructures.vectorization;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.NotCoveringRequiredVariantsException;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.evaluation.fitness.ReplayUtils;

import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.Spliterator;
import java.util.stream.IntStream;

public class VMHComputations {

    public static boolean markingBasedBooleanReplay(IntBuffer variant) {
        int next = 0;
        while (variant.hasRemaining()) {
            next = variant.get();
            if (next < 0) return false;
        }
        return next == 0;
    }

    public static IntStream localIndicesStream(VariantMarkingHistories vmh) {
        return vmh.getIndexSubset().streamMappingRange();
    }

    public static IntStream toLocal(VariantMarkingHistories vmh, BitMask mask) {
        return vmh.getIndexSubset().mapIndices(mask.stream());
    }

    public static EnumSet<OrderingRelation> orderingRelations(VariantMarkingHistories left, VariantMarkingHistories right) {
        if (!left.getIndexSubset().isSupersetOf(right.getIndexSubset()))
            throw new NotCoveringRequiredVariantsException();
        return IVSComputations.orderingRelationsOn(localIndicesStream(left), left.getData(), localIndicesStream(right), right.getData());
    }

    public static EnumSet<OrderingRelation> orderingRelationsOn(BitMask mask, VariantMarkingHistories left, VariantMarkingHistories right) {
        if (!left.getIndexSubset().covers(mask) || !right.getIndexSubset().covers(mask))
            throw new NotCoveringRequiredVariantsException();
        return IVSComputations.orderingRelationsOn(toLocal(left, mask), left.getData(), toLocal(right, mask), right.getData());
    }

    public static Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> indexedFitnessComputation(VariantMarkingHistories vmh) {
        return vmh.getData()
                  .getIndexedVectors()
                  .map(ii -> new IndexedItem<>(toLocal(vmh, ii.getIndex()), ReplayUtils.markingBasedReplay(ii.getItem())))
                  .spliterator();
    }

    public static Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> indexedFitnessComputationOn(VariantMarkingHistories vmh, BitMask variantMask) {
        return vmh.getData()
                  .getIndexedVectors(toLocal(vmh, variantMask))
                  .map(ii -> new IndexedItem<>(toLocal(vmh, ii.getIndex()), ReplayUtils.markingBasedReplay(ii.getItem())))
                  .spliterator();
    }

    private static int toLocal(VariantMarkingHistories vmh, int index) {
        return vmh.getIndexSubset().mapIndex(index);
    }

}
