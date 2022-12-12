package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.util.Pair;

import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

public class ForkJoinFitnessEvaluator extends AbstractBasicFitnessEvaluator {


    public ForkJoinFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource, ReplayComputationParameters replayComputationParameters) {
        super(multiEncodedLog, variantSubsetSource, replayComputationParameters);
    }

    public static class Builder extends AbstractBasicFitnessEvaluator.Builder {

        @Override
        protected ForkJoinFitnessEvaluator buildIfFullySatisfied() {
            return new ForkJoinFitnessEvaluator(multiEncodedLogSource.getData(), variantSubsetSource.getDelegate(), replayComputationParametersSource.getData());
        }
    }


    @Override
    public BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator = prepareSpliterator(place, consideredVariants);
        AbstractEnumSetReplayTask<ReplayUtils.ReplayOutcomes, BasicFitnessEvaluation> simpleReplayTask = ReplayUtils.createBasicReplayTask(spliterator, getVariantFrequencies()::get);
        return ReplayUtils.computeHere(simpleReplayTask);
    }

    @Override
    public DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator = prepareSpliterator(place, consideredVariants);
        AbstractEnumSetReplayTask<ReplayUtils.ReplayOutcomes, DetailedFitnessEvaluation> simpleReplayTask = ReplayUtils.createDetailedReplayTask(spliterator, getVariantFrequencies()::get);
        return ReplayUtils.computeHere(simpleReplayTask);
    }

    private Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> prepareSpliterator(Place place, BitMask consideredVariants) {
        IntUnaryOperator presetIndicator = ReplayUtils.presetIndicator(place);
        IntUnaryOperator postsetIndicator = ReplayUtils.postsetIndicator(place);

        Stream<IndexedItem<Pair<IntBuffer>>> stream = getIndexedItemStream();
        if (consideredVariants != null) stream = stream.filter(ip -> consideredVariants.get(ip.getIndex()));
        return stream.map(ip -> new IndexedItem<>(ip.getIndex(), ReplayUtils.variantReplay(ip.getItem()
                                                                                             .getT1(), presetIndicator, ip.getItem()
                                                                                                                          .getT2(), postsetIndicator)))
                     .spliterator();
    }


}
