package org.processmining.specpp.headless.batch;

import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;

public class SPECppModelInfo extends BatchedExecutionResult {

    public static final String[] COLUMN_NAMES = new String[]{"run identifier", "initial place count", "post processed place count"};
    private final int initialPlaceCount;
    private final int postProcessedPlaceCount;

    public SPECppModelInfo(String runIdentifier, SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp) {
        super(runIdentifier, "SPECppModelInfo");
        initialPlaceCount = specpp.getInitialResult() != null ? specpp.getInitialResult().size() : -1;
        postProcessedPlaceCount = specpp.getPostProcessedResult() != null ? specpp.getPostProcessedResult()
                                                                                  .getPlaces()
                                                                                  .size() : -1;
    }

    public SPECppModelInfo(String runIdentifier, int initialPlaceCount, int postProcessedPlaceCount) {
        super(runIdentifier, "SPECppModelInfo");
        this.initialPlaceCount = initialPlaceCount;
        this.postProcessedPlaceCount = postProcessedPlaceCount;
    }


    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String[] toRow() {
        return new String[]{runIdentifier, Integer.toString(initialPlaceCount), Integer.toString(postProcessedPlaceCount)};
    }
}
