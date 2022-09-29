package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;

public class PlaceAccepter<I extends AdvancedComposition<Place>> extends AcceptingComposer<Place, I, CollectionOfPlaces> {
    public PlaceAccepter(I composition) {
        super(composition, c -> new CollectionOfPlaces(c.toList()));
    }
}
