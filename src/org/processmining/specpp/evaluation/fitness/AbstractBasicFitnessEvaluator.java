package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.util.JavaTypingUtils;

import java.nio.IntBuffer;
import java.util.Spliterator;
import java.util.stream.Stream;

public abstract class AbstractBasicFitnessEvaluator extends AbstractFitnessEvaluator {

    protected ReplayComputationParameters replayComputationParameters;


    public static abstract class Builder extends ComponentSystemAwareBuilder<AbstractBasicFitnessEvaluator> {

        protected final DelegatingDataSource<MultiEncodedLog> multiEncodedLogSource = new DelegatingDataSource<>();
        protected final DelegatingDataSource<BitMask> variantSubsetSource = new DelegatingDataSource<>();

        protected final DelegatingDataSource<ReplayComputationParameters> replayComputationParametersSource = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(DataRequirements.CONSIDERED_VARIANTS, variantSubsetSource)
                                   .require(DataRequirements.ENC_LOG, multiEncodedLogSource)
                                   .require(ParameterRequirements.REPLAY_COMPUTATION, replayComputationParametersSource);
        }

    }


    public AbstractBasicFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource, ReplayComputationParameters replayComputationParameters) {
        super(multiEncodedLog, variantSubsetSource);
        this.replayComputationParameters = replayComputationParameters;
        globalComponentSystem().provide(EvaluationRequirements.evaluator(Place.class, BasicFitnessEvaluation.class, this::eval))
                               .provide(EvaluationRequirements.evaluator(Place.class, DetailedFitnessEvaluation.class, this::detailedEval))
                               .provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), BasicFitnessEvaluation.class, this::subsetEval))
                               .provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), DetailedFitnessEvaluation.class, this::detailedSubsetEval));


    }

    protected Spliterator<IndexedItem<Pair<IntBuffer>>> getIndexedItemSpliterator() {
        return getMultiEncodedLog().indexedSpliterator();
    }

    protected Stream<IndexedItem<Pair<IntBuffer>>> getIndexedItemStream() {
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
