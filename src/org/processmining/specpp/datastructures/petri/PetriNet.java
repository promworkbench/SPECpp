package org.processmining.specpp.datastructures.petri;

import org.processmining.specpp.base.Result;

import java.util.Set;

public class PetriNet implements Result {
    private final Set<Place> places;

    public PetriNet(Set<Place> places) {
        this.places = places;
    }

    public Set<Place> getPlaces() {
        return places;
    }

    @Override
    public String toString() {
        return "PetriNet{" +
                "places=" + places +
                '}';
    }
}
