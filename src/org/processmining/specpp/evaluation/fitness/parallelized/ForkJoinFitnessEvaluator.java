package org.processmining.specpp.evaluation.fitness.parallelized;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.evaluation.fitness.AbstractFullFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.IntUnaryOperator;

public class ForkJoinFitnessEvaluator extends AbstractFullFitnessEvaluator {


    public ForkJoinFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource, ReplayComputationParameters replayComputationParameters) {
        super(multiEncodedLog, variantSubsetSource, replayComputationParameters);
    }

    public static class Builder extends AbstractFullFitnessEvaluator.Builder {

        @Override
        protected ForkJoinFitnessEvaluator buildIfFullySatisfied() {
            return new ForkJoinFitnessEvaluator(multiEncodedLogSource.getData(), variantSubsetSource.getDelegate(), replayComputationParametersSource.getData());
        }

    }

    protected <R> R makeComputation(Place place, BitMask consideredVariants, BiFunction<Spliterator<IndexedItem<EnumSet<ReplayOutcome>>>, IntUnaryOperator, AbstractEnumSetReplayTask<ReplayOutcome, R>> creator) {
        Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> spliterator = prepareSpliterator(place, consideredVariants);
        AbstractEnumSetReplayTask<ReplayOutcome, R> task = creator.apply(spliterator, getVariantFrequencies()::get);
        return ReplayTasks.computeHere(task);
    }

    @Override
    public BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        return makeComputation(place, consideredVariants, ReplayTasks::createBasicReplayTask);
    }

    @Override
    public DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        return makeComputation(place, consideredVariants, ReplayTasks::createDetailedReplayTask);

    }

    @Override
    public ComprehensiveFitnessEvaluation comprehensiveComputation(Place place, BitMask consideredVariants) {
        return makeComputation(place, consideredVariants, ReplayTasks::createComprehensiveReplayTask);
    }


}
