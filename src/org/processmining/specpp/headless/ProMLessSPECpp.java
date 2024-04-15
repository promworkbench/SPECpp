package org.processmining.specpp.headless;

import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.config.SPECppConfigBundle;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.ExecutionEnvironment;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.util.FileUtils;

public class ProMLessSPECpp {

    public static void main(String[] args) {
        if (args.length < 2) return;
        String eventLogPath = args[0];// + ".xes";
        String resultPath = args[1];// + ".pnml";
        run(eventLogPath, resultPath);
    }

    public static ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> run(String logPath, String resultPath) {
        SPECppConfigBundle cfg = CodeDefinedConfigurationSample.createConfiguration();
        InputDataBundle data = InputDataBundle.loadAndProcess(logPath, cfg.getInputProcessingConfig());
        SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = SPECpp.build(cfg, data);

        ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution;
        try (ExecutionEnvironment ee = new ExecutionEnvironment(Runtime.getRuntime().availableProcessors())) {
            execution = ee.execute(specpp, ExecutionParameters.noTimeouts());
            ee.addCompletionCallback(execution, ex -> {
                ProMPetrinetWrapper petrinetWrapper = ex.getSPECpp().getPostProcessedResult();
                FileUtils.savePetrinetToPnml(resultPath, petrinetWrapper);
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return execution;
    }


}
