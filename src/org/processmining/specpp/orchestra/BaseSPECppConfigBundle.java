package org.processmining.specpp.orchestra;

public class BaseSPECppConfigBundle extends AbstractSPECppConfigBundle {
    public BaseSPECppConfigBundle() {
        super(new BaseDataExtractionConfig(), new BaseSPECppComponentConfig(), new BaseAlgorithmParameterConfig());
    }

    @Override
    public String getTitle() {
        return "Base Version";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }
}
