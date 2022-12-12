package org.processmining.specpp.config.parameters;

import org.processmining.specpp.componenting.data.ParameterRequirements;

public class DefaultParameters extends ParameterProvider {

    public DefaultParameters() {
        globalComponentSystem().provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWithStatic(OutputPathParameters.getDefault()))
                               .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWithStatic(SupervisionParameters.getDefault()))
                               .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWithStatic(TauFitnessThresholds.getDefault()))
                               .provide(ParameterRequirements.REPLAY_COMPUTATION.fulfilWithStatic(ReplayComputationParameters.getDefault()))
                               .provide(ParameterRequirements.IMPLICITNESS_TESTING.fulfilWithStatic(ImplicitnessTestingParameters.getDefault()))
                               .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWithStatic(PlaceGeneratorParameters.getDefault()));
    }

}
