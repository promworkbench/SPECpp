package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.ProMPetrinetBuilder;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;

public class ProMConverter implements PostProcessor<PetriNet, ProMPetrinetWrapper> {
    @Override
    public ProMPetrinetWrapper postProcess(PetriNet result) {
        ProMPetrinetBuilder builder = new ProMPetrinetBuilder(result);
        return builder.build();
    }

    @Override
    public Class<PetriNet> getInputClass() {
        return PetriNet.class;
    }

    @Override
    public Class<ProMPetrinetWrapper> getOutputClass() {
        return ProMPetrinetWrapper.class;
    }
}
