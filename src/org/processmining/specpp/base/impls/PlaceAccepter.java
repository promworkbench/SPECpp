package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;

public class PlaceAccepter<I extends AdvancedComposition<Place>> extends AcceptingComposer<Place, I, PetriNet> {
    public PlaceAccepter(I composition) {
        super(composition, c -> new PetriNet(c.toSet()));
    }
}
