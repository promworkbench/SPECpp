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
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.heuristics.CandidateScore;
import org.processmining.specpp.util.JavaTypingUtils;

import java.util.stream.Stream;

public abstract class UniwiredSelfLoopAdditionPostProcessing implements CollectionOfPlacesPostProcessor {

    protected final Evaluator<Place, BasicFitnessEvaluation> fitnessEvaluator;
    protected final HeuristicStrategy<Place, CandidateScore> candidateScorer;
    protected final TauFitnessThresholds fitnessThresholds;
    protected final IntEncodings<Transition> transitionIntEncodings;

    public UniwiredSelfLoopAdditionPostProcessing(IntEncodings<Transition> transitionIntEncodings, Evaluator<Place, BasicFitnessEvaluation> fitnessEvaluator, TauFitnessThresholds fitnessThresholds, HeuristicStrategy<Place, CandidateScore> candidateScorer) {
        this.fitnessEvaluator = fitnessEvaluator;
        this.candidateScorer = candidateScorer;
        this.fitnessThresholds = fitnessThresholds;
        this.transitionIntEncodings = transitionIntEncodings;
    }

    public static abstract class Builder extends ComponentSystemAwareBuilder<UniwiredSelfLoopAdditionPostProcessing> {

        protected final DelegatingEvaluator<Place, BasicFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();
        protected final DelegatingDataSource<HeuristicStrategy<Place, CandidateScore>> dfHeuristic = new DelegatingDataSource<>();
        protected final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();
        protected final DelegatingDataSource<IntEncodings<Transition>> transitionIntEncodings = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(EvaluationRequirements.BASIC_FITNESS, fitnessEvaluator)
                                   .require(DataRequirements.ENC_TRANS, transitionIntEncodings)
                                   .require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds)
                                   .require(DataRequirements.dataSource("heuristics.place.df", JavaTypingUtils.castClass(HeuristicStrategy.class)), dfHeuristic);
        }


    }

    protected static Place extendWithSelfLoop(Place input, Transition transition) {
        Place copy = input.copy();
        copy.preset().add(transition);
        copy.postset().add(transition);
        return copy;
    }

    protected static Place extendWithSelfLoops(Place input, Stream<Transition> transitions) {
        Place copy = input.copy();
        transitions.forEach(t -> {
            copy.preset().add(t);
            copy.postset().add(t);
        });
        return copy;
    }

}
