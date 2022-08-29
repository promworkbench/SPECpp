package org.processmining.specpp.headless;

import org.processmining.specpp.orchestra.BaseSPECppConfigBundle;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.orchestra.SPECppOperations;
import org.processmining.specpp.preprocessing.InputData;

public class ProMlessSPECpp {

    public static void main(String[] args) {
        if (args.length < 1) return;
        String eventLogPath = args[0];
        SPECppOperations.configureAndExecute(BaseSPECppConfigBundle::new, InputData.loadData(eventLogPath, PreProcessingParameters.getDefault()), true, true, true);
    }

}
