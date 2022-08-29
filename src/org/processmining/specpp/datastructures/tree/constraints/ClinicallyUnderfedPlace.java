package org.processmining.specpp.datastructures.tree.constraints;

import org.processmining.specpp.base.impls.CandidateConstraint;
import org.processmining.specpp.datastructures.petri.Place;

public class ClinicallyUnderfedPlace extends CandidateConstraint<Place> {

    public ClinicallyUnderfedPlace(Place affectedPlace) {
        super(affectedPlace);
    }

    @Override
    public String toString() {
        return "ClinicallyUnderfedPlace(" + getAffectedCandidate() + ")";
    }

}
