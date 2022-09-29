package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.ProMPetrinetBuilder;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;

public class ProMConverter implements PostProcessor<CollectionOfPlaces, ProMPetrinetWrapper> {
    @Override
    public ProMPetrinetWrapper postProcess(CollectionOfPlaces result) {
        ProMPetrinetBuilder builder = new ProMPetrinetBuilder(result);
        return builder.build();
    }

    @Override
    public Class<CollectionOfPlaces> getInputClass() {
        return CollectionOfPlaces.class;
    }

    @Override
    public Class<ProMPetrinetWrapper> getOutputClass() {
        return ProMPetrinetWrapper.class;
    }
}
