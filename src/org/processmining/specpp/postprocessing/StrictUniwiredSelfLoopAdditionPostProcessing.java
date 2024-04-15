package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.nodegen.WiringMatrix;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.FitnessThresholder;
import org.processmining.specpp.evaluation.heuristics.CandidateScore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StrictUniwiredSelfLoopAdditionPostProcessing extends UniwiredSelfLoopAdditionPostProcessing {
    public StrictUniwiredSelfLoopAdditionPostProcessing(IntEncodings<Transition> transitionIntEncodings, Evaluator<Place, BasicFitnessEvaluation> fitnessEvaluator, TauFitnessThresholds fitnessThresholds, HeuristicStrategy<Place, CandidateScore> candidateScorer) {
        super(transitionIntEncodings, fitnessEvaluator, fitnessThresholds, candidateScorer);
    }

    public static class Builder extends UniwiredSelfLoopAdditionPostProcessing.Builder {

        @Override
        protected StrictUniwiredSelfLoopAdditionPostProcessing buildIfFullySatisfied() {
            return new StrictUniwiredSelfLoopAdditionPostProcessing(transitionIntEncodings.getData(), fitnessEvaluator, fitnessThresholds.getData(), dfHeuristic.getData());
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

}
