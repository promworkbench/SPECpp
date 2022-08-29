package org.processmining.specpp.orchestra;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.traits.ProvidesParameters;

public class AdaptedAlgorithmParameterConfig implements AlgorithmParameterConfig {

    private final ProvidesParameters parameters;

    public AdaptedAlgorithmParameterConfig(ProvidesParameters changedParameters) {
        this.parameters = changedParameters;
    }

    @Override
    public void registerAlgorithmParameters(GlobalComponentRepository cr) {
        AlgorithmParameterConfig.super.registerAlgorithmParameters(cr);
        cr.overridingAbsorb(parameters.parameters());
    }
}
