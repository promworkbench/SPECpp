package org.processmining.specpp.orchestra;

import org.processmining.specpp.componenting.traits.ProvidesParameters;

public class CustomSPECppConfigBundle extends SPECppConfigBundle {
    @Override
    public String getTitle() {
        return "Custom Version";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }

    public CustomSPECppConfigBundle(ProvidesParameters customParameters) {
        super(new BaseDataExtractionConfig(), new BaseSPECppComponentConfig(), new AdaptedAlgorithmParameterConfig(customParameters));
    }
}
