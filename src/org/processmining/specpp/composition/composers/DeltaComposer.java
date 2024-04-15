package org.processmining.specpp.composition.composers;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.base.impls.AbstractQueueingComposer;
import org.processmining.specpp.base.impls.CandidateConstraint;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.config.parameters.DeltaComposerParameters;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.NonMutatingSetOperations;
import org.processmining.specpp.datastructures.encoding.WeightedBitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.specpp.datastructures.util.caching.BasicCache;
import org.processmining.specpp.datastructures.util.caching.ComputingCache;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.datastructures.util.caching.StackedCache;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.FittingVariantsEvaluation;
import org.processmining.specpp.util.JavaTypingUtils;

public class DeltaComposer<I extends AdvancedComposition<Place>, R extends Result> extends AbstractQueueingComposer<Place, I, R, CandidateConstraint<Place>> {

    private final DelegatingEvaluator<Place, FittingVariantsEvaluation> fittingVariantsEvaluator = new DelegatingEvaluator<>();
    private final DelegatingDataSource<BasicCache<Place, FittingVariantsEvaluation>> fittingVariantsCache = new DelegatingDataSource<>();

    private final DelegatingEvaluator<EvaluationParameterTuple2<Place, Integer>, DoubleScore> deltaAdaptationFunction = new DelegatingEvaluator<>();
    private final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();
    private final DelegatingDataSource<DeltaComposerParameters> deltaComposerParameters = new DelegatingDataSource<>();
    private final DelegatingDataSource<WeightedBitMask> currentlySupportedVariants = new DelegatingDataSource<>();
    private final DelegatingDataSource<IntVector> variantFrequencies = new DelegatingDataSource<>();
    private int currentTreeLevel;
    private final DelegatingDataSource<Integer> treeLevelSource = new DelegatingDataSource<>(() -> currentTreeLevel);
    private Evaluator<Place, FittingVariantsEvaluation> getFittingVariants;
    private int maxQueueSize;

    public DeltaComposer(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
        globalComponentSystem().require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds)
                               .require(EvaluationRequirements.PLACE_FITTING_VARIANTS, fittingVariantsEvaluator)
                               .require(EvaluationRequirements.DELTA_ADAPTATION_FUNCTION, deltaAdaptationFunction)
                               .require(ParameterRequirements.DELTA_COMPOSER_PARAMETERS, deltaComposerParameters)
                               .require(DataRequirements.VARIANT_FREQUENCIES, variantFrequencies)
                               .require(DataRequirements.dataSource("tree.current_level", Integer.class), treeLevelSource)
                               .provide(SupervisionRequirements.observable("postponing_composer.constraints", JavaTypingUtils.castClass(CandidateConstraint.class), getConstraintPublisher()));

        currentTreeLevel = 0;
        localComponentSystem().require(DataRequirements.dataSource("currently_supported_variants", WeightedBitMask.class), currentlySupportedVariants)
                              .require(DataRequirements.dataSource("cache.fitting_variants", JavaTypingUtils.castClass(BasicCache.class)), fittingVariantsCache);
    }

    @Override
    public void initSelf() {
        super.initSelf();
        DeltaComposerParameters parameters = deltaComposerParameters.getData();
        maxQueueSize = parameters.getMaxQueueSize();

        ComputingCache<Place, FittingVariantsEvaluation> cache = new ComputingCache<>(1_000, fittingVariantsEvaluator);
        if (fittingVariantsCache.isEmpty()) getFittingVariants = cache::get;
        else getFittingVariants = new StackedCache<>(fittingVariantsCache.getData(), cache)::get;
    }

    @Override
    protected CandidateDecision deliberateCandidate(Place candidate) {
        if (candidate.size() > currentTreeLevel) {
            currentTreeLevel = candidate.size();
            iteratePostponedCandidatesUntilNoChange();
        }
        return meetsCurrentDelta(candidate) ? CandidateDecision.Accept : CandidateDecision.Postpone;
    }

    @Override
    protected void postponeDecision(Place candidate) {
        if (postponedCandidates.size() < maxQueueSize) postponedCandidates.add(candidate);
    }

    @Override
    protected CandidateDecision reDeliberateCandidate(Place candidate) {
        return meetsCurrentDelta(candidate) ? CandidateDecision.Accept : CandidateDecision.Postpone;
    }

    private boolean meetsCurrentDelta(Place candidate) {
        FittingVariantsEvaluation fittingVariantsEvaluation = getFittingVariants.eval(candidate);
        Integer treeLevel = treeLevelSource.getData(); // more efficient: get once per postponed list traversal
        DoubleScore adaptedDelta = deltaAdaptationFunction.eval(new EvaluationParameterTuple2<>(candidate, treeLevel));
        double adaptedTau = fitnessThresholds.getData().getFittingThreshold() * adaptedDelta.getScore();
        WeightedBitMask supportedVariants = currentlySupportedVariants.getData();
        IntVector frequencies = variantFrequencies.getData();
        BitMask intersection = NonMutatingSetOperations.intersection(fittingVariantsEvaluation.getFittingVariants(), supportedVariants);
        double f = intersection.stream().mapToDouble(frequencies::getRelative).sum();
        return f >= supportedVariants.getWeight() - adaptedTau;
    }

    @Override
    protected void rejectCandidate(Place candidate) {

    }

    @Override
    protected void discardCandidate(Place candidate) {

    }

    @Override
    public void candidatesAreExhausted() {
        currentTreeLevel = 10;
        super.candidatesAreExhausted();
    }

    @Override
    public Class<CandidateConstraint<Place>> getPublishedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
    }
}
