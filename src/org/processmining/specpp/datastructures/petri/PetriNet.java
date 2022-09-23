package org.processmining.specpp.datastructures.petri;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import org.processmining.specpp.base.Result;

import java.util.Collection;

public class PetriNet implements Result {
    private final ImmutableCollection<Place> places;

    public PetriNet(Collection<Place> places) {
        this.places = ImmutableList.copyOf(places);
    }

    public ImmutableCollection<Place> getPlaces() {
        return places;
    }

    @Override
    public String toString() {
        return "PetriNet{" + "places=" + places + '}';
    }

    public int size() {
        return places.size();
    }
}
