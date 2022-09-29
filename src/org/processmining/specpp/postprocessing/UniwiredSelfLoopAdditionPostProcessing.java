package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.nodegen.WiringMatrix;
import org.processmining.specpp.evaluation.fitness.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.FitnessThresholder;
import org.processmining.specpp.evaluation.heuristics.CandidateScore;
import org.processmining.specpp.util.JavaTypingUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UniwiredSelfLoopAdditionPostProcessing implements CollectionOfPlacesPostProcessor {

    private final Evaluator<Place, BasicFitnessEvaluation> fitnessEvaluator;
    private final HeuristicStrategy<Place, CandidateScore> candidateScorer;
    private final TauFitnessThresholds fitnessThresholds;
    private final IntEncodings<Transition> transitionIntEncodings;

    public UniwiredSelfLoopAdditionPostProcessing(IntEncodings<Transition> transitionIntEncodings, Evaluator<Place, BasicFitnessEvaluation> fitnessEvaluator, TauFitnessThresholds fitnessThresholds, HeuristicStrategy<Place, CandidateScore> candidateScorer) {
        this.fitnessEvaluator = fitnessEvaluator;
        this.candidateScorer = candidateScorer;
        this.fitnessThresholds = fitnessThresholds;
        this.transitionIntEncodings = transitionIntEncodings;
    }

    public static class Builder extends ComponentSystemAwareBuilder<UniwiredSelfLoopAdditionPostProcessing> {

        private final DelegatingEvaluator<Place, BasicFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();
        private final DelegatingDataSource<HeuristicStrategy<Place, CandidateScore>> dfHeuristic = new DelegatingDataSource<>();
        private final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();
        private final DelegatingDataSource<IntEncodings<Transition>> transitionIntEncodings = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(EvaluationRequirements.BASIC_FITNESS, fitnessEvaluator)
                                   .require(DataRequirements.ENC_TRANS, transitionIntEncodings)
                                   .require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds)
                                   .require(DataRequirements.dataSource("heuristics.place.df", JavaTypingUtils.castClass(HeuristicStrategy.class)), dfHeuristic);
        }

        @Override
        protected UniwiredSelfLoopAdditionPostProcessing buildIfFullySatisfied() {
            return new UniwiredSelfLoopAdditionPostProcessing(transitionIntEncodings.getData(), fitnessEvaluator, fitnessThresholds.getData(), dfHeuristic.getData());
        }
    }


    @Override
    public CollectionOfPlaces postProcess(CollectionOfPlaces result) {
        WiringMatrix theWired = new WiringMatrix(transitionIntEncodings);
        result.getPlaces().forEach(theWired::wire);

        Set<Transition> possibleSelfLoopingTransitions = transitionIntEncodings.domainIntersection();
        List<Place> maximallyExtendedPlaces = result.getPlaces()
                                                    .stream()
                                                    .map(p -> extendWithSelfLoops(p, possibleSelfLoopingTransitions.stream()
                                                                                                                   .filter(t -> FitnessThresholder.isTauFitting(fitnessEvaluator.eval(extendWithSelfLoop(p, t)), fitnessThresholds))))
                                                    .sorted(Comparator.comparing(candidateScorer::computeHeuristic, candidateScorer.heuristicValuesComparator()))
                                                    .collect(Collectors.toList());

        ArrayList<Place> extended = new ArrayList<>(result.getPlaces());
        for (Place place : maximallyExtendedPlaces) {
            if (!theWired.isWired(place)) {
                extended.add(place);
                theWired.wire(place);
            }
        }

        return new CollectionOfPlaces(extended);
    }

    private static Place extendWithSelfLoop(Place input, Transition transition) {
        Place copy = input.copy();
        copy.preset().add(transition);
        copy.postset().add(transition);
        return copy;
    }

    private static Place extendWithSelfLoops(Place input, Stream<Transition> transitions) {
        Place copy = input.copy();
        transitions.forEach(t -> {
            copy.preset().add(t);
            copy.postset().add(t);
        });
        return copy;
    }

}
