package org.processmining.specpp.orchestra;

import org.processmining.specpp.config.parameters.TauDeltaParameters;

public class TauDeltaConfigBundle extends AbstractSPECppConfigBundle {
    @Override
    public String getTitle() {
        return "Tau Delta Variant";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }

    public TauDeltaConfigBundle() {
        super(new BaseDataExtractionConfig(), new TauDeltaComponentConfig(), new AdaptedAlgorithmParameterConfig(new TauDeltaParameters()));
    }
}
