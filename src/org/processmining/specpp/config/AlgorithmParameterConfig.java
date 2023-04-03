package org.processmining.specpp.config;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.traits.ProvidesParameters;

public interface AlgorithmParameterConfig {

    ProvidesParameters getParameters();

    default void registerAlgorithmParameters(GlobalComponentRepository cr) {
        cr.absorb(getParameters());
    }

}
