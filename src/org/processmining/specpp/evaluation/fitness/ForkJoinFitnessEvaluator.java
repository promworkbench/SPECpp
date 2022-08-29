package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.util.Tuple2;

import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

public class ForkJoinFitnessEvaluator extends AbstractBasicFitnessEvaluator {


    @Override
    public BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(BASIC_EVALUATION);
        Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator = prepareSpliterator(place, consideredVariants);
        AbstractEnumSetReplayTask<ReplayUtils.ReplayOutcomes, BasicFitnessEvaluation> simpleReplayTask = ReplayUtils.createBasicReplayTask(spliterator, getVariantFrequencies()::get);
        BasicFitnessEvaluation result = ReplayUtils.computeHere(simpleReplayTask);
        timeStopper.stop(BASIC_EVALUATION);
        return result;
    }

    @Override
    public DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(DETAILED_EVALUATION);
        Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator = prepareSpliterator(place, consideredVariants);
        AbstractEnumSetReplayTask<ReplayUtils.ReplayOutcomes, DetailedFitnessEvaluation> simpleReplayTask = ReplayUtils.createDetailedReplayTask(spliterator, getVariantFrequencies()::get);
        DetailedFitnessEvaluation result = ReplayUtils.computeHere(simpleReplayTask);
        timeStopper.stop(DETAILED_EVALUATION);
        return result;
    }

    private Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> prepareSpliterator(Place place, BitMask consideredVariants) {
        IntUnaryOperator presetIndicator = ReplayUtils.presetIndicator(place);
        IntUnaryOperator postsetIndicator = ReplayUtils.postsetIndicator(place);

        Stream<IndexedItem<Tuple2<IntBuffer, IntBuffer>>> stream = getIndexedItemStream();
        if (consideredVariants != null) stream = stream.filter(ip -> consideredVariants.get(ip.getIndex()));
        return stream.map(ip -> new IndexedItem<>(ip.getIndex(), ReplayUtils.variantReplay(ip.getItem()
                                                                                             .getT1(), presetIndicator, ip.getItem()
                                                                                                                          .getT2(), postsetIndicator)))
                     .spliterator();
    }


}
