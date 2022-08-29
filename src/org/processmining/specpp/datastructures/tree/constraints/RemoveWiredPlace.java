package org.processmining.specpp.datastructures.tree.constraints;

import org.processmining.specpp.datastructures.petri.Place;

public class RemoveWiredPlace extends WiringConstraint {
    public RemoveWiredPlace(Place affectedPlace) {
        super(affectedPlace);
    }
}
