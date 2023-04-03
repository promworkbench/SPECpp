package org.processmining.specpp.datastructures.tree.nodegen;

import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;

import java.util.HashSet;
import java.util.Set;

public class UnWiringMatrix extends WiringMatrix {

    protected final Set<Place> wiredPlaces;

    public UnWiringMatrix(IntEncodings<Transition> transitionEncodings) {
        super(transitionEncodings);
        wiredPlaces = new HashSet<>();
    }

    @Override
    public void wire(Place place) {
        super.wire(place);
        wiredPlaces.add(place);
    }

    private void recomputeWireSets() {
        for (Place p : wiredPlaces) {
            super.wire(p);
        }
    }

    @Override
    public void unwire(Place place) {
        wiredPlaces.remove(place);
        reset();
        recomputeWireSets();
    }


}
