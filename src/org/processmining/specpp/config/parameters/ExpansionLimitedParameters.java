package org.processmining.specpp.config.parameters;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;

public class ExpansionLimitedParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {
    public ExpansionLimitedParameters() {
        globalComponentSystem()
                .provide(ParameterRequirements.parameters(ParameterRequirements.TAU_FITNESS_THRESHOLDS, StaticDataSource.of(TauFitnessThresholds.tau(1))))
                .provide(ParameterRequirements.parameters(ParameterRequirements.PLACE_GENERATOR_PARAMETERS,
                        StaticDataSource.of(
                                new PlaceGeneratorParameters(5, true, false, true, true))));
    }
}
