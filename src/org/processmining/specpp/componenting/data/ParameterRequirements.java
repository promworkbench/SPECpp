package org.processmining.specpp.componenting.data;

import org.processmining.specpp.composition.DeltaComposerParameters;
import org.processmining.specpp.config.ExternalInitializationParameters;
import org.processmining.specpp.config.parameters.*;
import org.processmining.specpp.evaluation.fitness.ReplayComputationParameters;
import org.processmining.specpp.evaluation.heuristics.TreeHeuristicThreshold;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessTestingParameters;

public class ParameterRequirements {

    public static final ParameterRequirement<OutputPathParameters> OUTPUT_PATH_PARAMETERS = parameters("output_paths", OutputPathParameters.class);
    public static final ParameterRequirement<PlaceGeneratorParameters> PLACE_GENERATOR_PARAMETERS = parameters("placegenerator.parameters", PlaceGeneratorParameters.class);
    public static final ParameterRequirement<TauFitnessThresholds> TAU_FITNESS_THRESHOLDS = parameters("tau_fitness_thresholds", TauFitnessThresholds.class);
    public static final ParameterRequirement<DeltaParameters> DELTA_PARAMETERS = parameters("delta.parameters", DeltaParameters.class);
    public static final ParameterRequirement<SupervisionParameters> SUPERVISION_PARAMETERS = parameters("supervision.parameters", SupervisionParameters.class);

    public static final ParameterRequirement<ExecutionParameters> EXECUTION_PARAMETERS = parameters("execution.parameters", ExecutionParameters.class);
    public static final ParameterRequirement<ImplicitnessTestingParameters> IMPLICITNESS_TESTING = parameters("implicitness.parameters", ImplicitnessTestingParameters.class);
    public static final ParameterRequirement<ReplayComputationParameters> REPLAY_COMPUTATION = parameters("replay.parameters", ReplayComputationParameters.class);
    public static final ParameterRequirement<ExternalInitializationParameters> EXTERNAL_INITIALIZATION = parameters("external_initialization.parameters", ExternalInitializationParameters.class);
    public static final ParameterRequirement<DeltaComposerParameters> DELTA_COMPOSER_PARAMETERS = parameters("delta_composer.parameters", DeltaComposerParameters.class);
    public static ParameterRequirement<TreeHeuristicThreshold> TREE_HEURISTIC_THRESHOLD = parameters("tree.heuristic.parameters", TreeHeuristicThreshold.class);

    public static <P extends Parameters> ParameterRequirement<P> parameters(String label, Class<P> type) {
        return new ParameterRequirement<>(label, type);
    }

    public static <P extends Parameters> FulfilledDataRequirement<P> parameters(String label, Class<P> type, DataSource<P> dataSource) {
        return parameters(label, type).fulfilWith(dataSource);
    }

    public static <P extends Parameters> FulfilledDataRequirement<P> parameters(ParameterRequirement<P> requirement, DataSource<P> dataSource) {
        return requirement.fulfilWith(dataSource);
    }

}
