package org.processmining.specpp.datastructures.petri;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import org.processmining.specpp.base.Result;

import java.util.Collection;

public class CollectionOfPlaces implements Result {
    private final ImmutableCollection<Place> places;

    public CollectionOfPlaces(Collection<Place> places) {
        this.places = ImmutableList.copyOf(places);
    }

    public ImmutableCollection<Place> getPlaces() {
        return places;
    }

    @Override
    public String toString() {
        return "CollectionOfPlaces{" + places + '}';
    }

    public int size() {
        return places.size();
    }
}
