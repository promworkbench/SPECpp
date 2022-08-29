package org.processmining.specpp.config.parameters;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.orchestra.AlgorithmParameterConfig;

public class ProMAlgorithmParameterConfig implements AlgorithmParameterConfig {


    @Override
    public void registerAlgorithmParameters(GlobalComponentRepository cr) {
        cr.provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWith(StaticDataSource.of(OutputPathParameters.getDefault())))
          .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(new SupervisionParameters(false))))
          .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWith(StaticDataSource.of(TauFitnessThresholds.getDefault())))
          .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWith(StaticDataSource.of(PlaceGeneratorParameters.getDefault())));
    }
}
