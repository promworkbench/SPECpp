package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;

public interface CollectionOfPlacesPostProcessor extends PostProcessor<CollectionOfPlaces, CollectionOfPlaces> {

    @Override
    default Class<CollectionOfPlaces> getInputClass() {
        return CollectionOfPlaces.class;
    }

    @Override
    default Class<CollectionOfPlaces> getOutputClass() {
        return CollectionOfPlaces.class;
    }
}
