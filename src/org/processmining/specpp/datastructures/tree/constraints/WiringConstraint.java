package org.processmining.specpp.datastructures.tree.constraints;

import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.base.GenerationConstraint;

public class WiringConstraint extends WiredPlace implements GenerationConstraint {

    public WiringConstraint(Place affectedPlace) {
        super(affectedPlace);
    }

}
