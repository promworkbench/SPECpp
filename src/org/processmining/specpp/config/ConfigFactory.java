package org.processmining.specpp.config;

import org.processmining.specpp.componenting.traits.ProvidesParameters;

public class ConfigFactory {

    public static InputProcessingConfig create(PreProcessingParameters preProcessingParameters, DataExtractionParameters dataExtractionParameters) {
        return new InputProcessingConfigImpl(preProcessingParameters, dataExtractionParameters);
    }

    public static AlgorithmParameterConfig create(ProvidesParameters... providesParameters) {
        return new AlgorithmParameterConfigImpl(providesParameters);
    }

    public static SPECppConfigBundle create(PreProcessingParameters preProcessingParameters, DataExtractionParameters dataExtractionParameters, ComponentConfig componentConfig, ProvidesParameters... providesParameters) {
        return create(create(preProcessingParameters, dataExtractionParameters), componentConfig, create(providesParameters));
    }

    public static SPECppConfigBundle create(InputProcessingConfig inputProcessingConfig, ComponentConfig componentConfig, AlgorithmParameterConfig algorithmParameterConfig) {
        return new SPECppConfigBundleImpl(inputProcessingConfig, componentConfig, algorithmParameterConfig);
    }

}
