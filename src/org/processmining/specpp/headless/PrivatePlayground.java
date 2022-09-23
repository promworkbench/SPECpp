package org.processmining.specpp.headless;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.config.parameters.*;
import org.processmining.specpp.orchestra.CustomSPECppConfigBundle;
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
        weirdExecution();
    }

    public static void singleExecution() {
        SPECppOperations.configureAndExecute(() -> new CustomSPECppConfigBundle(new MyParameters(.5, 5)), InputData.loadData(PrivatePaths.toPath("temp/flatten_resources.xes"), PreProcessingParameters.getDefault()), false);
    }

    private static class MyParameters extends ParameterProvider {
        public MyParameters(double tau, int treeDepth) {
            globalComponentSystem().provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(SupervisionParameters.instrumentAll(true, true))))
                                   .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWith(StaticDataSource.of(TauFitnessThresholds.tau(tau))))
                                   .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWith(StaticDataSource.of(new PlaceGeneratorParameters(treeDepth, true, false, false, false))));
        }
    }

    public static void weirdExecution() {
        SPECppOperations.configureAndExecute(PlaceFocusedSPECppConfigBundle::new, InputData.loadData(PrivatePaths.toPath("temp/Customer.xes"), PreProcessingParameters.getDefault()), false);
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
                globalComponentSystem().provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(SupervisionParameters.instrumentAll(false, true))))
                                       .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWith(StaticDataSource.of(new PlaceGeneratorParameters(5, true, false, true, true))))
                                       .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWith(StaticDataSource.of(TauFitnessThresholds.tau(tau))))
                                       .provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWith(StaticDataSource.of(OutputPathParameters.ofPrefix("cfg_id_" + configId + "$"))));
            }
        }
        return new P();
    }

}
