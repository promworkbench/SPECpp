package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.evaluation.fitness.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.FitnessThresholder;
import org.processmining.specpp.evaluation.heuristics.CandidateScore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NaiveUniwiredSelfLoopAdditionPostProcessing extends UniwiredSelfLoopAdditionPostProcessing {
    public NaiveUniwiredSelfLoopAdditionPostProcessing(IntEncodings<Transition> transitionIntEncodings, Evaluator<Place, BasicFitnessEvaluation> fitnessEvaluator, TauFitnessThresholds fitnessThresholds, HeuristicStrategy<Place, CandidateScore> candidateScorer) {
        super(transitionIntEncodings, fitnessEvaluator, fitnessThresholds, candidateScorer);
    }

    public static class Builder extends UniwiredSelfLoopAdditionPostProcessing.Builder {

        @Override
        protected UniwiredSelfLoopAdditionPostProcessing buildIfFullySatisfied() {
            return new NaiveUniwiredSelfLoopAdditionPostProcessing(transitionIntEncodings.getData(), fitnessEvaluator, fitnessThresholds.getData(), dfHeuristic.getData());
        }
    }

    @Override
    public CollectionOfPlaces postProcess(CollectionOfPlaces result) {
        Set<Transition> possibleSelfLoopingTransitions = transitionIntEncodings.domainIntersection();
        List<Place> maximallyExtendedPlaces = result.getPlaces()
                                                    .stream()
                                                    .map(p -> extendWithSelfLoops(p, possibleSelfLoopingTransitions.stream()
                                                                                                                   .filter(t -> FitnessThresholder.isTauFitting(fitnessEvaluator.eval(extendWithSelfLoop(p, t)), fitnessThresholds))))
                                                    .sorted(Comparator.comparing(candidateScorer::computeHeuristic, candidateScorer.heuristicValuesComparator()))
                                                    .collect(Collectors.toList());

        ArrayList<Place> extended = new ArrayList<>(result.getPlaces());
        extended.addAll(maximallyExtendedPlaces);

        return new CollectionOfPlaces(extended);
    }

}
