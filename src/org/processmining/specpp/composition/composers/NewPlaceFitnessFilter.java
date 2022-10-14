package org.processmining.specpp.composition.composers;

import org.apache.commons.collections4.BidiMap;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
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
import org.processmining.specpp.datastructures.tree.constraints.ClinicallyOverfedPlace;
import org.processmining.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.evaluation.fitness.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.DetailedFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.FitnessThresholder;

import java.util.Map;
import java.util.stream.Collectors;

public class NewPlaceFitnessFilter<I extends CompositionComponent<Place>, R extends Result> extends PlaceFitnessFilter<I, R> {

    DelegatingDataSource<Log> logSource = new DelegatingDataSource<>();
    DelegatingDataSource<BidiMap<Activity, Transition>> actTransMapping = new DelegatingDataSource<>();
    private Map<Transition, BitMask> activationMap;
    private IntVector frequencies;
    private Map<Transition, Integer> activationFrequencies;

    public NewPlaceFitnessFilter(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
        globalComponentSystem().require(DataRequirements.RAW_LOG, logSource)
                               .require(DataRequirements.ACT_TRANS_MAPPING, actTransMapping);
    }

    @Override
    protected void initSelf() {
        super.initSelf();
        Log log = logSource.getData();
        frequencies = log.getVariantFrequencies();
        BidiMap<Activity, Transition> mapping = actTransMapping.getData();
        activationMap = mapping.values().stream().collect(Collectors.toMap(t -> t, t -> new BitMask()));
        for (IndexedVariant indexedVariant : log) {
            for (Activity activity : indexedVariant.getVariant()) {
                activationMap.get(mapping.get(activity)).set(indexedVariant.getIndex());
            }
        }
        activationFrequencies = activationMap.entrySet()
                                                                      .stream()
                                                                      .collect(Collectors.toMap(Map.Entry::getKey, t -> t.getValue()
                                                                                                                       .stream()
                                                                                                                       .map(frequencies::get)
                                                                                                                       .sum()));
    }

    @Override
    public void accept(Place place) {
        DetailedFitnessEvaluation eval = fitnessEvaluator.eval(place);
        BitMask fittingVariants = eval.getFittingVariants();

        double new_fitness = place.incidentTransitions().stream().mapToDouble(incidentTransition -> {
            BitMask intersection = NonMutatingSetOperations.intersection(fittingVariants, activationMap.get(incidentTransition));
            double f = intersection.stream().map(frequencies::get).sum();
            return f / activationFrequencies.get(incidentTransition);
        }).min().orElse(0.0);

        BasicFitnessEvaluation fitness = eval.getFractionalEvaluation();
        TauFitnessThresholds thresholds = fitnessThresholds.getData();
        if (FitnessThresholder.isUnderfed(fitness, thresholds)) {
            constraintEvents.observe(new ClinicallyUnderfedPlace(place));
            gotFiltered(place);
        } else if (FitnessThresholder.isOverfed(fitness, thresholds)) {
            constraintEvents.observe(new ClinicallyOverfedPlace(place));
            gotFiltered(place);
        } else if (FitnessThresholder.isTauFitting(new_fitness, thresholds)) {
            fitnessCache.put(place, eval);
            forward(place);
        }
    }
}
