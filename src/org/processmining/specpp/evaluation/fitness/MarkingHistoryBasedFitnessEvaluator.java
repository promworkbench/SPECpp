package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.vectorization.VMHComputations;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.parallelized.AbstractEnumSetReplayTask;
import org.processmining.specpp.evaluation.fitness.parallelized.ReplayTasks;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.IntUnaryOperator;

public class MarkingHistoryBasedFitnessEvaluator extends AbstractFullFitnessEvaluator {

    private final Evaluator<? super Place, ? extends VariantMarkingHistories> historyMaker;

    public MarkingHistoryBasedFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource, ReplayComputationParameters replayComputationParameters, Evaluator<? super Place, ? extends VariantMarkingHistories> historyMaker) {
        super(multiEncodedLog, variantSubsetSource, replayComputationParameters);
        this.historyMaker = historyMaker;
    }


    public static class Builder extends AbstractFullFitnessEvaluator.Builder {
        private final DelegatingEvaluator<Place, VariantMarkingHistories> historyMakerSource = new DelegatingEvaluator<>();

        @Override
        protected MarkingHistoryBasedFitnessEvaluator buildIfFullySatisfied() {
            return new MarkingHistoryBasedFitnessEvaluator(multiEncodedLogSource.getData(), variantSubsetSource.getDelegate(), replayComputationParametersSource.getData(), historyMakerSource.getDelegate());
        }
    }

    public static final TaskDescription basic = new TaskDescription("Basic Marking Based Fitness Evaluation");
    public static final TaskDescription detailed = new TaskDescription("Detailed Marking Based Fitness Evaluation");
    public static final TaskDescription comprehensive = new TaskDescription("Comprehensive Marking Based Fitness Evaluation");

    protected <R> R makeComputation(Place place, BitMask consideredVariants, BiFunction<Spliterator<IndexedItem<EnumSet<ReplayOutcome>>>, IntUnaryOperator, AbstractEnumSetReplayTask<ReplayOutcome, R>> creator) {
        VariantMarkingHistories h = historyMaker.eval(place);
        Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> spliterator = VMHComputations.indexedFitnessComputationOn(h, consideredVariants);
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

    /*
    These implementations do not look right
    public static BasicFitnessEvaluation computeBasicEvaluationHere(Spliterator<IndexedItem<BasicFitnessStatus>> spliterator, IntUnaryOperator vectorFrequency) {
        int enumLength = BasicFitnessStatus.values().length;
        int[] counts = new int[enumLength];
        spliterator.forEachRemaining(rr -> counts[rr.getItem().ordinal()] += vectorFrequency.applyAsInt(rr.getIndex()));
        return BasicFitnessEvaluation.ofCounts(new EnumCounts<>(counts));
    }

    public static DetailedFitnessEvaluation computeDetailedEvaluationHere(Spliterator<IndexedItem<BasicFitnessStatus>> spliterator, IntUnaryOperator vectorFrequency) {
        int enumLength = BasicFitnessStatus.values().length;
        int[] counts = new int[enumLength];
        BitMask fitting = new BitMask();
        spliterator.forEachRemaining(rr -> {
            int i = rr.getIndex();
            BasicFitnessStatus fitnessStatus = rr.getItem();
            counts[fitnessStatus.ordinal()] += vectorFrequency.applyAsInt(i);
            if (fitnessStatus == BasicFitnessStatus.FITTING) fitting.set(i);
        });
        BasicFitnessEvaluation ev = BasicFitnessEvaluation.ofCounts(new EnumCounts<>(counts));
        return new DetailedFitnessEvaluation(fitting, ev);
    }

    public static ComprehensiveFitnessEvaluation computeComprehensiveEvaluationHere(Spliterator<IndexedItem<BasicFitnessStatus>> spliterator, IntUnaryOperator vectorFrequency) {
        int[] counts = ReplayUtils.createCountArray();
        BitMask[] outcomes = ReplayUtils.createBitMaskArray();
        spliterator.forEachRemaining(rr -> {
            int i = rr.getIndex();
            BasicFitnessStatus fitnessStatus = rr.getItem();
            counts[fitnessStatus.ordinal()] += vectorFrequency.applyAsInt(i);
            outcomes[fitnessStatus.ordinal()].set(i);
        });
        BasicFitnessEvaluation ev = BasicFitnessEvaluation.ofCounts(new EnumCounts<>(counts));
        return new ComprehensiveFitnessEvaluation(new EnumMapping<>(outcomes), ev);
    }
     */


    @Override
    public String toString() {
        return "MarkingHistoryBasedFitnessEvaluator(" + historyMaker.getClass().getSimpleName() + ")";
    }


}
