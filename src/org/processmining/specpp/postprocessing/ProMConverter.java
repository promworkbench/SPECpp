package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.ProMPetrinetBuilder;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;

public class ProMConverter implements PostProcessor<PetriNet, ProMPetrinetWrapper> {
    @Override
    public ProMPetrinetWrapper postProcess(PetriNet result) {
        ProMPetrinetBuilder builder = new ProMPetrinetBuilder(result.getPlaces());
        return builder.build();
    }

}
