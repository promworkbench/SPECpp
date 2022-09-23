package org.processmining.specpp.orchestra;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.preprocessing.InputDataBundle;

public abstract class AbstractSPECppConfigBundle implements SPECppConfigBundle {

    private final DataExtractionConfig dataExtractionConfig;
    private final SPECppComponentConfig componentConfig;
    private final AlgorithmParameterConfig algorithmParameterConfig;

    public AbstractSPECppConfigBundle(DataExtractionConfig dataExtractionConfig, SPECppComponentConfig componentConfig, AlgorithmParameterConfig algorithmParameterConfig) {
        this.dataExtractionConfig = dataExtractionConfig;
        this.componentConfig = componentConfig;
        this.algorithmParameterConfig = algorithmParameterConfig;
    }

    @Override
    public void instantiate(GlobalComponentRepository cr, InputDataBundle bundle) {
        dataExtractionConfig.registerDataSources(cr, bundle);
        componentConfig.registerConfigurations(cr);
        algorithmParameterConfig.registerAlgorithmParameters(cr);
    }

}
