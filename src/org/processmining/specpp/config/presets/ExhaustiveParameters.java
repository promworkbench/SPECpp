package org.processmining.specpp.config.presets;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;

public class ExhaustiveParameters extends ParameterProvider {

    public ExhaustiveParameters() {
        globalComponentSystem().provide(ParameterRequirements.parameters(ParameterRequirements.TAU_FITNESS_THRESHOLDS, StaticDataSource.of(TauFitnessThresholds.tau(1))))
                               .provide(ParameterRequirements.parameters(ParameterRequirements.PLACE_GENERATOR_PARAMETERS, StaticDataSource.of(PlaceGeneratorParameters.getDefault())));
    }

}
