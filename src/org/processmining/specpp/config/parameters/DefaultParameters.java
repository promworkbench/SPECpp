package org.processmining.specpp.config.parameters;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.evaluation.fitness.ReplayComputationParameters;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessTestingParameters;

public class DefaultParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {

    public DefaultParameters() {
        globalComponentSystem().provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWith(StaticDataSource.of(OutputPathParameters.getDefault())))
                               .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(SupervisionParameters.getDefault())))
                               .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWith(StaticDataSource.of(TauFitnessThresholds.getDefault())))
                               .provide(ParameterRequirements.REPLAY_COMPUTATION.fulfilWith(StaticDataSource.of(ReplayComputationParameters.getDefault())))
                               .provide(ParameterRequirements.IMPLICITNESS_TESTING.fulfilWith(StaticDataSource.of(ImplicitnessTestingParameters.getDefault())))
                               .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWith(StaticDataSource.of(PlaceGeneratorParameters.getDefault())));
    }

}
