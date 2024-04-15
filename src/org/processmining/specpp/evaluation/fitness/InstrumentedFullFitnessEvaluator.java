package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;
import org.processmining.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.specpp.supervision.observations.performance.TimeStopper;

public class InstrumentedFullFitnessEvaluator extends AbstractFullFitnessEvaluator {

    public static final TaskDescription BASIC_EVALUATION = new TaskDescription("Basic Fitness Evaluation");
    public static final TaskDescription DETAILED_EVALUATION = new TaskDescription("Detailed Fitness Evaluation");
    public static final TaskDescription COMPREHENSIVE_EVALUATION = new TaskDescription("Comprehensive Fitness Evaluation");
    private final AbstractFullFitnessEvaluator delegate;
    private final TimeStopper timeStopper = new TimeStopper();

    public InstrumentedFullFitnessEvaluator(AbstractFullFitnessEvaluator delegate) {
        super(delegate.getMultiEncodedLog(), delegate.getVariantSubsetSource(), delegate.replayComputationParameters);
        globalComponentSystem().provide(SupervisionRequirements.observable("evaluator.performance", PerformanceEvent.class, timeStopper));
        this.delegate = delegate;
    }

    @Override
    public BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(BASIC_EVALUATION);
        BasicFitnessEvaluation evaluation = delegate.basicComputation(place, consideredVariants);
        timeStopper.stop(BASIC_EVALUATION);
        return evaluation;
    }

    @Override
    public DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(DETAILED_EVALUATION);
        DetailedFitnessEvaluation evaluation = delegate.detailedComputation(place, consideredVariants);
        timeStopper.stop(DETAILED_EVALUATION);
        return evaluation;
    }

    @Override
    public ComprehensiveFitnessEvaluation comprehensiveComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(COMPREHENSIVE_EVALUATION);
        ComprehensiveFitnessEvaluation evaluation = delegate.comprehensiveComputation(place, consideredVariants);
        timeStopper.stop(COMPREHENSIVE_EVALUATION);
        return evaluation;
    }

}
