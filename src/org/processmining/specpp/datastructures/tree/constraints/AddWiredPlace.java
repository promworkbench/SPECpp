package org.processmining.specpp.datastructures.tree.constraints;

import org.processmining.specpp.datastructures.petri.Place;

public class AddWiredPlace extends WiringConstraint {
    public AddWiredPlace(Place affectedPlace) {
        super(affectedPlace);
    }
}
