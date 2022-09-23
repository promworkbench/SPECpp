package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.datastructures.petri.PetriNet;

public interface PetriNetPostProcessor extends PostProcessor<PetriNet, PetriNet> {

    @Override
    default Class<PetriNet> getInputClass() {
        return PetriNet.class;
    }

    @Override
    default Class<PetriNet> getOutputClass() {
        return PetriNet.class;
    }
}
