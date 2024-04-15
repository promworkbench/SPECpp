package org.processmining.specpp.composition.composers;

import org.apache.commons.collections4.BidiMap;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.NonMutatingSetOperations;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.arraybacked.EnumMapping;
import org.processmining.specpp.datastructures.util.caching.BasicCache;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;
import org.processmining.specpp.util.JavaTypingUtils;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public abstract class AbstractActivityActivationAwareFitnessFilter<I extends CompositionComponent<Place>, R extends Result> extends ConstrainingFilteringPlaceComposer<I, R> {

    protected final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();
    protected final DelegatingEvaluator<Place, ComprehensiveFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();
    protected final BasicCache<Place, ComprehensiveFitnessEvaluation> fitnessCache;

    private final DelegatingDataSource<Log> logSource = new DelegatingDataSource<>();
    private final DelegatingDataSource<BidiMap<Activity, Transition>> actTransMapping = new DelegatingDataSource<>();
    private Map<Transition, BitMask> activationMap;
    private IntVector frequencies;
    private Map<Transition, Integer> activationFrequencies;

    public AbstractActivityActivationAwareFitnessFilter(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
        globalComponentSystem()
                .require(DataRequirements.RAW_LOG, logSource)
                .require(DataRequirements.ACT_TRANS_MAPPING, actTransMapping)
                .require(EvaluationRequirements.COMPREHENSIVE_FITNESS, fitnessEvaluator)
                .require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds);

        fitnessCache = new BasicCache<>();

        localComponentSystem()
                .provide(DataRequirements.dataSource("cache.fitting_variants", JavaTypingUtils.castClass(BasicCache.class), StaticDataSource.of(fitnessCache)))
                .provide(DataRequirements.dataSource("cache.comprehensive_fitness", JavaTypingUtils.castClass(BasicCache.class), StaticDataSource.of(fitnessCache)));

    }

    @Override
    protected void initSelf() {
        Log log = logSource.getData();
        frequencies = log.getVariantFrequencies();
        BidiMap<Activity, Transition> mapping = actTransMapping.getData();
        activationMap = mapping.values().stream().collect(Collectors.toMap(t -> t, t -> new BitMask()));
        for (IndexedVariant indexedVariant : log) {
            for (Activity activity : indexedVariant.getVariant()) {
                activationMap.get(mapping.get(activity)).set(indexedVariant.getIndex());
            }
        }
        activationFrequencies = activationMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, t -> t.getValue().stream().map(frequencies::get).sum()));
    }

    public DoubleStream conditionalOutcomesStream(Place place, EnumMapping<ReplayOutcome, BitMask> replayOutcomes, ReplayOutcome outcome) {
        return place.incidentTransitions().stream().mapToDouble(incidentTransition -> {
            BitMask intersection = NonMutatingSetOperations.intersection(replayOutcomes.get(outcome), activationMap.get(incidentTransition));
            double f = intersection.stream().map(frequencies::get).sum();
            return f / activationFrequencies.get(incidentTransition);
        });
    }


}
