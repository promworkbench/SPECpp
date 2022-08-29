package org.processmining.specpp.evaluation.implicitness;

import org.processmining.specpp.datastructures.petri.Place;

public class ReplaceableByImplicitness implements ImplicitnessRating {

    protected final Place p1, p2, p3;

    public ReplaceableByImplicitness(Place p1, Place p2, Place p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Place getP1() {
        return p1;
    }

    public Place getP2() {
        return p2;
    }

    public Place getP3() {
        return p3;
    }

    public Place getCandidate() {
        return getP1();
    }

    public Place getExisting() {
        return getP2();
    }

    public Place getReplacement() {
        return getP3();
    }

}
