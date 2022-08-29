package org.processmining.specpp.datastructures.tree.constraints;

import org.processmining.specpp.base.impls.CandidateConstraint;
import org.processmining.specpp.datastructures.petri.Place;

public class WiredPlace extends CandidateConstraint<Place> {

    public WiredPlace(Place affectedPlace) {
        super(affectedPlace);
    }
}
