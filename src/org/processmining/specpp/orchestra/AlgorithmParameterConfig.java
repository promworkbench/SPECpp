package org.processmining.specpp.orchestra;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.parameters.DefaultParameters;

public interface AlgorithmParameterConfig {

    default void registerAlgorithmParameters(GlobalComponentRepository cr) {
        cr.absorb(new DefaultParameters());
    }

}
