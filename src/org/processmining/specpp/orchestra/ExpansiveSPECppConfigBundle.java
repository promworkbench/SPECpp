package org.processmining.specpp.orchestra;

public class ExpansiveSPECppConfigBundle extends AbstractSPECppConfigBundle {
    @Override
    public String getTitle() {
        return "Fully Instrumented Version";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }

    public ExpansiveSPECppConfigBundle() {
        super(new BaseDataExtractionConfig(), new ExpansiveSPECppComponentConfig(), new BaseAlgorithmParameterConfig());
    }
}
