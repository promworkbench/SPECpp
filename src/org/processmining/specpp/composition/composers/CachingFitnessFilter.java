package org.processmining.specpp.composition.composers;

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
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.caching.BasicCache;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;
import org.processmining.specpp.util.JavaTypingUtils;

public abstract class CachingFitnessFilter<I extends CompositionComponent<Place>, R extends Result> extends ConstrainingFilteringPlaceComposer<I, R> {

    protected final DelegatingEvaluator<Place, DetailedFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();
    protected final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();
    protected final BasicCache<Place, DetailedFitnessEvaluation> fitnessCache;

    public CachingFitnessFilter(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
        fitnessCache = new BasicCache<>();
        globalComponentSystem()
                .require(EvaluationRequirements.DETAILED_FITNESS, fitnessEvaluator)
                .require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds);
        localComponentSystem()
                .provide(DataRequirements.dataSource("cache.fitting_variants", JavaTypingUtils.castClass(BasicCache.class), StaticDataSource.of(fitnessCache)))
                .provide(DataRequirements.dataSource("cache.detailed_fitness", JavaTypingUtils.castClass(BasicCache.class), StaticDataSource.of(fitnessCache)));
    }



}
