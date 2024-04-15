package org.processmining.specpp.headless;

import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.config.SPECppConfigBundle;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.ExecutionEnvironment;
import org.processmining.specpp.orchestra.SPECppOutputtingUtils;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.util.PublicPaths;

public class BasicSupervisedSPECpp {

    public static void main(String[] args) throws InterruptedException {
        String path = PublicPaths.SAMPLE_EVENTLOG_2;
        SPECppConfigBundle cfg = CodeDefinedConfigurationSample.createConfiguration();
        InputDataBundle data = InputDataBundle.loadAndProcess(path, cfg.getInputProcessingConfig());
        SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = null;
        try (ExecutionEnvironment ee = new ExecutionEnvironment()) {
            SPECppOutputtingUtils.preSetup(cfg, data, true);
            specpp = SPECpp.build(cfg, data);
            SPECppOutputtingUtils.postSetup(specpp, true);
            ee.execute(specpp, ExecutionParameters.noTimeouts());
            SPECppOutputtingUtils.duringExecution(specpp, true);
        } catch (InterruptedException ignored) {
        }
        SPECppOutputtingUtils.postExecution(specpp, true, true, true);
    }


}
