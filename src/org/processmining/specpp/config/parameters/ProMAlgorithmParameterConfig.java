package org.processmining.specpp.config.parameters;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.evaluation.fitness.ReplayComputationParameters;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessTestingParameters;
import org.processmining.specpp.orchestra.AlgorithmParameterConfig;

public class ProMAlgorithmParameterConfig implements AlgorithmParameterConfig {


    @Override
    public void registerAlgorithmParameters(GlobalComponentRepository cr) {
        cr.provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWithStatic(OutputPathParameters.getDefault()))
          .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWithStatic(SupervisionParameters.instrumentNone(false, false)))
          .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWithStatic(TauFitnessThresholds.getDefault()))
          .provide(ParameterRequirements.REPLAY_COMPUTATION.fulfilWithStatic(ReplayComputationParameters.getDefault()))
          .provide(ParameterRequirements.IMPLICITNESS_TESTING.fulfilWithStatic(ImplicitnessTestingParameters.getDefault()))
          .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWithStatic(PlaceGeneratorParameters.getDefault()));
    }
}
