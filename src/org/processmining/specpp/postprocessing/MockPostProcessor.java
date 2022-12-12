package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;

public class MockPostProcessor implements PostProcessor<CollectionOfPlaces, CollectionOfPlaces> {

    @Override
    public CollectionOfPlaces postProcess(CollectionOfPlaces result) {
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {

        }
        return result;
    }

    @Override
    public Class<CollectionOfPlaces> getInputClass() {
        return CollectionOfPlaces.class;
    }

    @Override
    public Class<CollectionOfPlaces> getOutputClass() {
        return CollectionOfPlaces.class;
    }
}
