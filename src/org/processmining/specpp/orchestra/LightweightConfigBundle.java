package org.processmining.specpp.orchestra;

import org.processmining.specpp.config.parameters.LightweightParameters;

public class LightweightConfigBundle extends AbstractSPECppConfigBundle {
    public LightweightConfigBundle() {
        super(new BaseDataExtractionConfig(), new LightweightComponentConfig(), new AdaptedAlgorithmParameterConfig(new LightweightParameters()));
    }

    @Override
    public String getTitle() {
        return "Lightweight Version";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }
}
