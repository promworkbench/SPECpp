package org.processmining.specpp.composition;

import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.config.parameters.ImplicitnessTestingParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.NonMutatingSetOperations;
import org.processmining.specpp.datastructures.encoding.WeightedBitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.ComputingCache;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;
import org.processmining.specpp.evaluation.implicitness.BooleanImplicitness;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.specpp.evaluation.implicitness.ReplayBasedImplicitnessCalculator;
import org.processmining.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.specpp.supervision.observations.performance.TimeStopper;
import org.processmining.specpp.util.JavaTypingUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StatefulPlaceComposition extends BasePlaceComposition implements OffersPlaceCompositionState, OffersImplicitness {
    public static final TaskDescription REPLAY_BASED_CONCURRENT_IMPLICITNESS = new TaskDescription("Concurrent Replay Based Implicitness");
    protected final Evaluator<Place, VariantMarkingHistories> historyMaker;
    protected final Map<Place, VariantMarkingHistories> histories;
    protected final Map<Place, BitMask> locallySupportedVariants;

    protected Evaluator<Place, ImplicitnessRating> implicitnessRater;
    private final DelegatingDataSource<ImplicitnessTestingParameters> implicitnessTestingParameters = new DelegatingDataSource<>();
    protected WeightedBitMask currentlySupportedVariants;
    protected final DelegatingDataSource<BitMask> consideredVariants = new DelegatingDataSource<>();
    protected final DelegatingDataSource<IntVector> variantFrequencies = new DelegatingDataSource<>();
    protected final DelegatingEvaluator<EvaluationParameterTuple2<Place, Collection<Place>>, BooleanImplicitness> externalImplicitnessCalculator = new DelegatingEvaluator<>();

    protected final TimeStopper timeStopper = new TimeStopper();

    public StatefulPlaceComposition() {
        histories = new HashMap<>();
        locallySupportedVariants = new HashMap<>();
        DelegatingEvaluator<Place, VariantMarkingHistories> pureEvaluator = new DelegatingEvaluator<>();
        globalComponentSystem().require(ParameterRequirements.IMPLICITNESS_TESTING, implicitnessTestingParameters)
                               .require(EvaluationRequirements.PLACE_MARKING_HISTORY, pureEvaluator)
                               .require(DataRequirements.CONSIDERED_VARIANTS, consideredVariants)
                               .require(DataRequirements.VARIANT_FREQUENCIES, variantFrequencies)
                               .require(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), BooleanImplicitness.class), externalImplicitnessCalculator)
                               .provide(SupervisionRequirements.observable("concurrent_implicitness.performance", PerformanceEvent.class, timeStopper));
        ComputingCache<Place, VariantMarkingHistories> cache = new ComputingCache<>(100, pureEvaluator);
        historyMaker = cache::get;
        localComponentSystem().provide(DataRequirements.dataSource("currently_supported_variants", WeightedBitMask.class, this::getCurrentlySupportedVariants))
                              .provide(EvaluationRequirements.PLACE_IMPLICITNESS.fulfilWith(this::rateImplicitness))
                              .provide(DataRequirements.dataSource("marking_histories_cache", JavaTypingUtils.castClass(Evaluator.class), StaticDataSource.of(cache.readOnlyGet())));
    }


    @Override
    public void initSelf() {
        ImplicitnessTestingParameters params = implicitnessTestingParameters.getData();
        if (params.getVersion() == ImplicitnessTestingParameters.CIPRVersion.ReplayBased) {
            Evaluator<Place, ImplicitnessRating> temp = null;
            switch (params.getSubLogRestriction()) {
                case None:
                    temp = p -> {
                        VariantMarkingHistories h = historyMaker.eval(p);
                        return ReplayBasedImplicitnessCalculator.replaySubregionImplicitness(p, h, histories);
                    };
                    break;
                case FittingOnAcceptedPlacesAndEvaluatedPlace:
                    temp = p -> {
                        VariantMarkingHistories h = historyMaker.eval(p);
                        BitMask intersection = NonMutatingSetOperations.intersection(getCurrentlySupportedVariants(), h.getPerfectlyFittingVariants());
                        return ReplayBasedImplicitnessCalculator.replaySubregionImplicitnessOn(intersection, p, h, histories);
                    };
                    break;
                case MerelyFittingOnEvaluatedPair:
                    temp = p -> {
                        VariantMarkingHistories h = historyMaker.eval(p);
                        return ReplayBasedImplicitnessCalculator.replaySubregionImplicitnessLocally(p, h, histories);
                    };
                    break;
            }
            Evaluator<Place, ImplicitnessRating> finalTemp = temp;
            implicitnessRater = p -> {
                timeStopper.start(REPLAY_BASED_CONCURRENT_IMPLICITNESS);
                ImplicitnessRating rating = finalTemp.apply(p);
                timeStopper.stop(REPLAY_BASED_CONCURRENT_IMPLICITNESS);
                return rating;
            };
        } else if (params.getVersion() == ImplicitnessTestingParameters.CIPRVersion.LPBased)
            implicitnessRater = p -> externalImplicitnessCalculator.eval(new EvaluationParameterTuple2<>(p, candidates)); // almost impossible to be less efficient with LP handling (by recreating the LP each call instead of managing the state here)

        resetCurrentlySupportedVariants(consideredVariants.getData());
    }

    private void resetCurrentlySupportedVariants(BitMask bm) {
        IntVector frequencies = variantFrequencies.getData();
        currentlySupportedVariants = new WeightedBitMask(bm, frequencies::getRelative);
    }

    @Override
    public void accept(Place place) {
        super.accept(place);
        VariantMarkingHistories h = historyMaker.eval(place);
        histories.put(place, h);
        BitMask supportedVariants = h.getPerfectlyFittingVariants();
        locallySupportedVariants.put(place, supportedVariants);
        currentlySupportedVariants.intersection(supportedVariants);
    }

    @Override
    public WeightedBitMask getCurrentlySupportedVariants() {
        return currentlySupportedVariants;
    }

    @Override
    public ImplicitnessRating rateImplicitness(Place place) {
        return implicitnessRater.eval(place);
    }

    @Override
    public void remove(Place candidate) {
        super.remove(candidate);
        histories.remove(candidate);
        locallySupportedVariants.remove(candidate);

        if (locallySupportedVariants.isEmpty()) {
            resetCurrentlySupportedVariants(consideredVariants.getData());
        } else {
            // TODO hella inefficient. Possibly compose result from per place info in a smarter way
            Iterator<BitMask> iterator = locallySupportedVariants.values().iterator();
            BitMask result = iterator.next().copy();
            while (iterator.hasNext()) {
                BitMask next = iterator.next();
                result.intersection(next);
            }
            resetCurrentlySupportedVariants(result);
        }
    }

}
