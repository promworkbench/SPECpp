package org.processmining.specpp.config;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.config.parameters.DefaultParameters;
import org.processmining.specpp.config.parameters.ParameterProvider;

public interface AlgorithmParameterConfig {

    ProvidesParameters getParameters();

    default void registerAlgorithmParameters(GlobalComponentRepository cr) {
        cr.absorb(getParameters());
    }

}
