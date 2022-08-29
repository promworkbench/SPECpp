package org.processmining.specpp.evaluation.implicitness;

import org.processmining.specpp.datastructures.petri.Place;

public class ReplaceExaminedPlace extends ReplaceableByImplicitness {

    public ReplaceExaminedPlace(Place p1, Place p2, Place p3) {
        super(p1, p2, p3);
    }

    @Override
    public String toString() {
        return "ReplaceExaminedPlace(" + p1 + ", " + p2 + ", " + p3 + ")";
    }
}
