package org.processmining.specpp.config;

public class SPECppConfigBundleImpl implements SPECppConfigBundle {

    private final InputProcessingConfig inputProcessingConfig;
    private final ComponentConfig componentConfig;
    private final AlgorithmParameterConfig algorithmParameterConfig;

    public SPECppConfigBundleImpl(InputProcessingConfig inputProcessingConfig, ComponentConfig componentConfig, AlgorithmParameterConfig algorithmParameterConfig) {
        this.inputProcessingConfig = inputProcessingConfig;
        this.componentConfig = componentConfig;
        this.algorithmParameterConfig = algorithmParameterConfig;
    }

    @Override
    public InputProcessingConfig getInputProcessingConfig() {
        return inputProcessingConfig;
    }

    @Override
    public ComponentConfig getComponentConfig() {
        return componentConfig;
    }

    @Override
    public AlgorithmParameterConfig getAlgorithmParameterConfig() {
        return algorithmParameterConfig;
    }

    @Override
    public String toString() {
        return "SPECppConfigBundle{" +
                "inputProcessingConfig=" + inputProcessingConfig +
                ", componentConfig=" + componentConfig +
                ", algorithmParameterConfig=" + algorithmParameterConfig +
                '}';
    }
}
