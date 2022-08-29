package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.specpp.util.JavaTypingUtils;

import java.nio.IntBuffer;
import java.util.Spliterator;
import java.util.stream.Stream;

public abstract class AbstractBasicFitnessEvaluator extends AbstractFitnessEvaluator {


    public static final TaskDescription BASIC_EVALUATION = new TaskDescription("Basic Fitness Evaluation");
    public static final TaskDescription DETAILED_EVALUATION = new TaskDescription("Detailed Fitness Evaluation");

    public AbstractBasicFitnessEvaluator() {
        globalComponentSystem()
                .provide(EvaluationRequirements.evaluator(Place.class, BasicFitnessEvaluation.class, this::eval))
                .provide(EvaluationRequirements.evaluator(Place.class, DetailedFitnessEvaluation.class, this::detailedEval))
                .provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), BasicFitnessEvaluation.class, this::subsetEval))
                .provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), DetailedFitnessEvaluation.class, this::detailedSubsetEval));

    }

    protected Spliterator<IndexedItem<Tuple2<IntBuffer, IntBuffer>>> getIndexedItemSpliterator() {
        return getMultiEncodedLog().indexedSpliterator();
    }

    protected Stream<IndexedItem<Tuple2<IntBuffer, IntBuffer>>> getIndexedItemStream() {
        return getMultiEncodedLog().indexedStream(false);
    }

    protected IntVector getVariantFrequencies() {
        return getMultiEncodedLog().getPresetEncodedLog().getVariantFrequencies();
    }


    public BasicFitnessEvaluation subsetEval(EvaluationParameterTuple2<Place, BitMask> tuple) {
        return basicComputation(tuple.getT1(), tuple.getT2());
    }

    public DetailedFitnessEvaluation detailedSubsetEval(EvaluationParameterTuple2<Place, BitMask> tuple) {
        return detailedComputation(tuple.getT1(), tuple.getT2());
    }


    public BasicFitnessEvaluation eval(Place place) {
        return basicComputation(place, getConsideredVariants());
    }

    public DetailedFitnessEvaluation detailedEval(Place place) {
        return detailedComputation(place, getConsideredVariants());
    }

    protected abstract BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants);

    protected abstract DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants);

}
