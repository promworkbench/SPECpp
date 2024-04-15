package org.processmining.specpp.headless;

import org.processmining.specpp.config.ConfigFactory;
import org.processmining.specpp.config.DataExtractionParameters;
import org.processmining.specpp.config.PreProcessingParameters;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.util.PublicPaths;

public class SampleData {

    public static InputDataBundle sample_1() {
        return InputDataBundle.loadAndProcess(PublicPaths.SAMPLE_EVENTLOG_2, ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault()));
    }


}
