package org.processmining.specpp.headless;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.specpp.config.parameters.SupervisionParameters;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.orchestra.BaseSPECppConfigBundle;
import org.processmining.specpp.orchestra.PlaceFocusedSPECppConfigBundle;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.orchestra.SPECppOperations;
import org.processmining.specpp.preprocessing.InputData;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.util.PrivatePaths;

import java.util.LinkedList;
import java.util.List;

public class PrivatePlayground {

    public static void main(String[] args) {
        singleExecution();
    }

    public static void singleExecution() {
        SPECppOperations.configureAndExecute(BaseSPECppConfigBundle::new, InputData.loadData(PrivatePaths.toPath(PrivatePaths.WILWILLES_REDUCED_NO_PARALELLISM), PreProcessingParameters.getDefault()), false);
    }

    private static class PureTauAdaptation extends AbstractGlobalComponentSystemUser implements ProvidesParameters {
        public PureTauAdaptation(double tau) {
            globalComponentSystem().provide(ParameterRequirements.parameters(ParameterRequirements.SUPERVISION_PARAMETERS, StaticDataSource.of(new SupervisionParameters(false))))
                                   .provide(ParameterRequirements.parameters(ParameterRequirements.TAU_FITNESS_THRESHOLDS, StaticDataSource.of(TauFitnessThresholds.tau(tau))));
        }
    }

    public static void weirdExecution() {
        SPECppOperations.configureAndExecute(PlaceFocusedSPECppConfigBundle::new, InputData.loadData(PrivatePaths.toPath(PrivatePaths.BPI12), PreProcessingParameters.getDefault()), true);
    }

    public static void multiParameterExecution() {
        DataSource<InputDataBundle> dataSource = InputData.loadData(PrivatePaths.toPath(PrivatePaths.ROAD_TRAFFIC_FINE_MANAGEMENT_PROCESS), PreProcessingParameters.getDefault());
        List<ProvidesParameters> list = createTauParameterList(1, .9, .75, .5, .3, .2, 0);
        SPECppOperations.configureAndExecuteMultiple(dataSource, list, false);
    }

    public static void multiSingleParameterExecution() {
        DataSource<InputDataBundle> dataSource = InputData.loadData(PrivatePaths.toPath(PrivatePaths.ROAD_TRAFFIC_FINE_MANAGEMENT_PROCESS), PreProcessingParameters.getDefault());
        List<ProvidesParameters> list = createTauParameterList(.5);
        SPECppOperations.configureAndExecuteMultiple(dataSource, list, false);
    }

    private static List<ProvidesParameters> createTauParameterList(double... taus) {
        LinkedList<ProvidesParameters> list = new LinkedList<>();
        for (int i = 0; i < taus.length; i++) {
            list.add(anonymous(i, taus[i]));
        }
        return list;
    }

    private static ProvidesParameters anonymous(int configId, double tau) {
        class P extends AbstractGlobalComponentSystemUser implements ProvidesParameters {
            public P() {
                globalComponentSystem().provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(new SupervisionParameters(false))))
                                       .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWith(StaticDataSource.of(new PlaceGeneratorParameters(5, true, false, true, true))))
                                       .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWith(StaticDataSource.of(TauFitnessThresholds.tau(tau))))
                                       .provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWith(StaticDataSource.of(OutputPathParameters.ofPrefix("cfg_id_" + configId + "$"))));
            }
        }
        return new P();
    }

}
