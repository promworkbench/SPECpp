package org.processmining.specpp.datastructures.tree.nodegen;

import org.processmining.specpp.datastructures.petri.Place;

public interface WiringTester extends PotentialExpansionsFilter, ExpansionStopper {
    void wire(Place place);

    void unwire(Place place);

    boolean isWired(Place place);

    @Override
    default boolean notAllowedToExpand(PlaceNode placeNode) {
        Place place = placeNode.getPlace();
        return isWired(place);
    }

}
