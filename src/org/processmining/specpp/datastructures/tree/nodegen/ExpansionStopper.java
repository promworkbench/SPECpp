package org.processmining.specpp.datastructures.tree.nodegen;

public interface ExpansionStopper {

    boolean notAllowedToExpand(PlaceNode placeNode);

}
