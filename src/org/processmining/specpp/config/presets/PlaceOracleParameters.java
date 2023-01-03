package org.processmining.specpp.config.presets;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.specpp.config.parameters.SupervisionParameters;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;

public class PlaceOracleParameters extends ParameterProvider {
    public PlaceOracleParameters() {
        globalComponentSystem().provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWithStatic(new PlaceGeneratorParameters(5, true, false, true, true)))
                               .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(SupervisionParameters.instrumentNone(false, true))))
                               .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWith(StaticDataSource.of(new TauFitnessThresholds(0.5))));
    }
}
