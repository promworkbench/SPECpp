package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.config.parameters.ImplicitnessTestingParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.NonMutatingSetOperations;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class ReplayBasedImplicitnessPostProcessing extends ImplicitnessPostProcessor {

    protected final Evaluator<Place, VariantMarkingHistories> markingHistoriesEvaluator;

    public ReplayBasedImplicitnessPostProcessing(Evaluator<Place, VariantMarkingHistories> markingHistoriesEvaluator, BitMask consideredVariants, ImplicitnessTestingParameters parameters) {
        super(consideredVariants, parameters);
        this.markingHistoriesEvaluator = markingHistoriesEvaluator;
    }


    public static class Builder extends ImplicitnessPostProcessor.Builder {

        protected final DelegatingEvaluator<Place, VariantMarkingHistories> evaluatorDelegator = new DelegatingEvaluator<>();

        public Builder() {
            globalComponentSystem().require(EvaluationRequirements.PLACE_MARKING_HISTORY, evaluatorDelegator);
        }

        @Override
        public ReplayBasedImplicitnessPostProcessing buildIfFullySatisfied() {
            return new ReplayBasedImplicitnessPostProcessing(evaluatorDelegator, consideredVariantsSource.getData(), parametersSource.getData());
        }
    }

    @Override
    public CollectionOfPlaces postProcess(CollectionOfPlaces result) {
        Set<Place> places = new HashSet<>(result.getPlaces());

        Map<Place, VariantMarkingHistories> histories = places.stream()
                                                              .parallel()
                                                              .map(p -> new ImmutableTuple2<>(p, markingHistoriesEvaluator.eval(p)))
                                                              .collect(Collectors.toMap(Tuple2::getT1, Tuple2::getT2));
        Function<VariantMarkingHistories, Predicate<VariantMarkingHistories>> predicate;
        predicate = getMarkingHistoriesPredicateFunction(histories);

        Set<Place> exclusionZone = new HashSet<>();
        for (Place place : places) {
            VariantMarkingHistories history = histories.get(place);
            Predicate<VariantMarkingHistories> implicitnessPredicate = predicate.apply(history);

            if (histories.entrySet()
                         .stream()
                         .filter(e -> !e.getKey().equals(place))
                         .map(Map.Entry::getValue)
                         .anyMatch(implicitnessPredicate)) {
                exclusionZone.add(place);
                histories.remove(place);
            }

        }

        places.removeAll(exclusionZone);
        return new CollectionOfPlaces(places);
    }

    protected Function<VariantMarkingHistories, Predicate<VariantMarkingHistories>> getMarkingHistoriesPredicateFunction(Map<Place, VariantMarkingHistories> histories) {
        Function<VariantMarkingHistories, Predicate<VariantMarkingHistories>> predicate;
        switch (parameters.getSubLogRestriction()) {
            case FittingOnAcceptedPlacesAndEvaluatedPlace:
                predicate = history -> {
                    // the fitting sub log may expand after an iteration
                    BitMask fittingSubLog = histories.values()
                                                     .stream()
                                                     .map(VariantMarkingHistories::getPerfectlyFittingVariants)
                                                     .reduce(consideredVariants.copy(), (acc, bm) -> {
                                                         acc.intersection(bm);
                                                         return acc;
                                                     });
                    return h -> history.gtOn(fittingSubLog, h);
                };
                break;
            case MerelyFittingOnEvaluatedPair:
                predicate = history -> h -> {
                    BitMask mask = NonMutatingSetOperations.intersection(history.getPerfectlyFittingVariants(), h.getPerfectlyFittingVariants());
                    return history.gtOn(mask, h);
                };
                break;
            default:
                predicate = history -> h -> history.gtOn(consideredVariants, h);
        }
        return predicate;
    }

    public static class Interruptible extends ReplayBasedImplicitnessPostProcessing {

        public static class Builder extends ReplayBasedImplicitnessPostProcessing.Builder {
            @Override
            public ReplayBasedImplicitnessPostProcessing buildIfFullySatisfied() {
                return new Interruptible(evaluatorDelegator, consideredVariantsSource.getData(), parametersSource.getData());
            }
        }

        public Interruptible(Evaluator<Place, VariantMarkingHistories> markingHistoriesEvaluator, BitMask consideredVariants, ImplicitnessTestingParameters parameters) {
            super(markingHistoriesEvaluator, consideredVariants, parameters);
        }

        @Override
        public CollectionOfPlaces postProcess(CollectionOfPlaces result) {
            Set<Place> places = new HashSet<>(result.getPlaces());

            Map<Place, VariantMarkingHistories> histories = places.stream()
                                                                  .parallel()
                                                                  .map(p -> new ImmutableTuple2<>(p, markingHistoriesEvaluator.eval(p)))
                                                                  .collect(Collectors.toMap(Tuple2::getT1, Tuple2::getT2));
            Function<VariantMarkingHistories, Predicate<VariantMarkingHistories>> predicate;
            predicate = getMarkingHistoriesPredicateFunction(histories);

            Set<Place> exclusionZone = new HashSet<>();
            for (Place place : places) {
                VariantMarkingHistories history = histories.get(place);
                Predicate<VariantMarkingHistories> implicitnessPredicate = predicate.apply(history);

                if (Thread.currentThread().isInterrupted()) return null; // purposefully not clearing interrupt flag

                if (histories.entrySet()
                             .stream()
                             .filter(e -> !e.getKey().equals(place))
                             .map(Map.Entry::getValue)
                             .anyMatch(implicitnessPredicate)) {
                    exclusionZone.add(place);
                    histories.remove(place);
                }

            }

            places.removeAll(exclusionZone);
            return new CollectionOfPlaces(places);
        }
    }

}
