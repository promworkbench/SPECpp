package org.processmining.specpp.evaluation.fitness.parallelized;

import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.function.IntUnaryOperator;

public class ReplayTasks {


    public static AbstractEnumSetReplayTask<ReplayOutcome, BasicFitnessEvaluation> createBasicReplayTask(Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> spliterator, IntUnaryOperator variantFrequencyMapper) {
        return new BasicReplayTask(spliterator, variantFrequencyMapper);
    }

    public static AbstractEnumSetReplayTask<ReplayOutcome, DetailedFitnessEvaluation> createDetailedReplayTask(Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> spliterator, IntUnaryOperator variantFrequencyMapper) {
        return new DetailedReplayTask(spliterator, variantFrequencyMapper);
    }

    public static AbstractEnumSetReplayTask<ReplayOutcome, ComprehensiveFitnessEvaluation> createComprehensiveReplayTask(Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> spliterator, IntUnaryOperator variantFrequencyMapper) {
        return new ComprehensiveReplayTask(spliterator, variantFrequencyMapper);
    }

    public static <R> R computeHere(AbstractEnumSetReplayTask<ReplayOutcome, R> task) {
        return task.computeHere();
    }

    public static <R> R computeForkJoinLike(AbstractEnumSetReplayTask<ReplayOutcome, R> task) {
        task.fork();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }



}
