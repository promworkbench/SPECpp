package org.processmining.specpp.componenting.data;

import org.processmining.specpp.config.parameters.*;

public class ParameterRequirements {

    public static final ParameterRequirement<OutputPathParameters> OUTPUT_PATH_PARAMETERS = parameters("output_paths", OutputPathParameters.class);
    public static final ParameterRequirement<PlaceGeneratorParameters> PLACE_GENERATOR_PARAMETERS = parameters("placegenerator.parameters", PlaceGeneratorParameters.class);
    public static final ParameterRequirement<TauFitnessThresholds> TAU_FITNESS_THRESHOLDS = parameters("tau_fitness_thresholds", TauFitnessThresholds.class);
    public static final ParameterRequirement<DeltaParameters> DELTA_PARAMETERS = parameters("base_delta", DeltaParameters.class);
    public static final ParameterRequirement<SupervisionParameters> SUPERVISION_PARAMETERS = parameters("supervision.parameters", SupervisionParameters.class);

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
