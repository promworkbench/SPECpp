package org.processmining.specpp.prom.plugins;

import org.processmining.specpp.config.InputProcessingConfig;
import org.processmining.specpp.prom.mvc.config.ProMConfig;

import java.util.Objects;

public class ProMSPECppConfig {

    private final InputProcessingConfig inputProcessingConfig;
    private final ProMConfig proMConfig;

    public ProMSPECppConfig(InputProcessingConfig inputProcessingConfig, ProMConfig proMConfig) {
        this.inputProcessingConfig = inputProcessingConfig;
        this.proMConfig = proMConfig;
    }

    public InputProcessingConfig getInputDataConfig() {
        return inputProcessingConfig;
    }

    @Override
    public String toString() {
        return "ProMSPECppConfig{" + "inputDataConfig=" + inputProcessingConfig + ", proMConfig=" + proMConfig + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProMSPECppConfig that = (ProMSPECppConfig) o;

        if (!Objects.equals(inputProcessingConfig, that.inputProcessingConfig)) return false;
        return Objects.equals(proMConfig, that.proMConfig);
    }

    @Override
    public int hashCode() {
        int result = inputProcessingConfig != null ? inputProcessingConfig.hashCode() : 0;
        result = 31 * result + (proMConfig != null ? proMConfig.hashCode() : 0);
        return result;
    }

    public ProMConfig getProMConfig() {
        return proMConfig;
    }
}
