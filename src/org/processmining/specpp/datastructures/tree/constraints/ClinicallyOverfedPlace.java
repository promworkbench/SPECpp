package org.processmining.specpp.datastructures.tree.constraints;

import org.processmining.specpp.base.impls.CandidateConstraint;
import org.processmining.specpp.datastructures.petri.Place;

public class ClinicallyOverfedPlace extends CandidateConstraint<Place> {

    public ClinicallyOverfedPlace(Place affectedPlace) {
        super(affectedPlace);
    }

    @Override
    public String toString() {
        return "ClinicallyOverfedPlace(" + getAffectedCandidate() + ")";
    }

}
