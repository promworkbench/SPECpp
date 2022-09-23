package org.processmining.specpp.orchestra;

import org.processmining.specpp.config.parameters.UniwiredParameters;

public class UniwiredConfigBundle extends AbstractSPECppConfigBundle {
    @Override
    public String getTitle() {
        return "Uniwired Variant";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }

    public UniwiredConfigBundle() {
        super(new BaseDataExtractionConfig(), new UniwiredComponentConfig(), new AdaptedAlgorithmParameterConfig(new UniwiredParameters()));
    }
}
