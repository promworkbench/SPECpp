package org.processmining.specpp.orchestra;

import org.processmining.specpp.config.parameters.ProMAlgorithmParameterConfig;

public class ProMSPECppConfigBundle extends AbstractSPECppConfigBundle {


    public ProMSPECppConfigBundle() {
        super(new BaseDataExtractionConfig(), new BaseSPECppComponentConfig(), new ProMAlgorithmParameterConfig());
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
