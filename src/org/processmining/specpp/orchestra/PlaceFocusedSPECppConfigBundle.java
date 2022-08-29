package org.processmining.specpp.orchestra;

import org.processmining.specpp.config.parameters.PlaceFocusParameters;

public class PlaceFocusedSPECppConfigBundle extends SPECppConfigBundle {
    @Override
    public String getTitle() {
        return "Place Focused Variant";
    }

    @Override
    public String getDescription() {
        return "This configuration employs the most lightweight components as possible to focus on merely discovering fitting places, not an implicit place-free Petri net.";
    }

    public PlaceFocusedSPECppConfigBundle() {
        super(new BaseDataExtractionConfig(), new PlaceFocussedComponentConfig(), new AdaptedAlgorithmParameterConfig(new PlaceFocusParameters()));
    }
}
