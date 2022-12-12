package org.processmining.specpp.config;

import java.util.Objects;

public class InputProcessingConfigImpl implements InputProcessingConfig {

    private final PreProcessingParameters preProcessingParameters;
    private final PreProcessingStrategy preProcessingStrategy;
    private final DataExtractionParameters dataExtractionParameters;
    private final DataExtractionStrategy dataExtractionStrategy;

    public InputProcessingConfigImpl(PreProcessingParameters preProcessingParameters, DataExtractionParameters dataExtractionParameters) {
        this.preProcessingParameters = preProcessingParameters;
        this.dataExtractionParameters = dataExtractionParameters;
        preProcessingStrategy = new BasePreProcessingStrategy();
        dataExtractionStrategy = new BaseDataExtractionStrategy();
    }

    @Override
    public String toString() {
        return "InputProcessingConfig{" +
                "preProcessingParameters=" + preProcessingParameters +
                ", preProcessingStrategy=" + preProcessingStrategy +
                ", dataExtractionParameters=" + dataExtractionParameters +
                ", dataExtractionStrategy=" + dataExtractionStrategy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InputProcessingConfigImpl that = (InputProcessingConfigImpl) o;

        if (!Objects.equals(preProcessingParameters, that.preProcessingParameters))
            return false;
        if (!Objects.equals(preProcessingStrategy, that.preProcessingStrategy))
            return false;
        if (!Objects.equals(dataExtractionParameters, that.dataExtractionParameters))
            return false;
        return Objects.equals(dataExtractionStrategy, that.dataExtractionStrategy);
    }

    @Override
    public int hashCode() {
        int result = preProcessingParameters != null ? preProcessingParameters.hashCode() : 0;
        result = 31 * result + (preProcessingStrategy != null ? preProcessingStrategy.hashCode() : 0);
        result = 31 * result + (dataExtractionParameters != null ? dataExtractionParameters.hashCode() : 0);
        result = 31 * result + (dataExtractionStrategy != null ? dataExtractionStrategy.hashCode() : 0);
        return result;
    }

    @Override
    public PreProcessingParameters getPreProcessingParameters() {
        return preProcessingParameters;
    }

    @Override
    public PreProcessingStrategy getPreProcessingStrategy() {
        return preProcessingStrategy;
    }

    @Override
    public DataExtractionParameters getDataExtractionParameters() {
        return dataExtractionParameters;
    }

    @Override
    public DataExtractionStrategy getDataExtractionStrategy() {
        return dataExtractionStrategy;
    }
}
