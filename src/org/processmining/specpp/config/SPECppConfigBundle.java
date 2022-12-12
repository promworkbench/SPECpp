package org.processmining.specpp.config;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.preprocessing.InputDataBundle;

public interface SPECppConfigBundle {

    InputProcessingConfig getInputProcessingConfig();

    ComponentConfig getComponentConfig();

    AlgorithmParameterConfig getAlgorithmParameterConfig();

    default void instantiate(GlobalComponentRepository cr, InputDataBundle bundle) {
        getInputProcessingConfig().instantiate(cr, bundle);
        getComponentConfig().registerConfigurations(cr);
        getAlgorithmParameterConfig().registerAlgorithmParameters(cr);
    }

}
