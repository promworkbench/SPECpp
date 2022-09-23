package org.processmining.specpp.prom.plugins;

import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.prom.mvc.config.ProMConfig;

public class ProMSPECppConfig {

    private final PreProcessingParameters preProcessingParameters;
    private final ProMConfig proMConfig;

    public ProMSPECppConfig(PreProcessingParameters preProcessingParameters, ProMConfig proMConfig) {
        this.preProcessingParameters = preProcessingParameters;
        this.proMConfig = proMConfig;
    }

    public PreProcessingParameters getPreProcessingParameters() {
        return preProcessingParameters;
    }

    public ProMConfig getProMConfig() {
        return proMConfig;
    }
}
