package org.processmining.specpp.datastructures.tree.constraints;

import org.processmining.specpp.datastructures.tree.heuristic.SubtreeCutoffConstraint;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;

public class CullPostsetChildren extends SubtreeCutoffConstraint<PlaceNode> {

    public CullPostsetChildren(PlaceNode affectedPlaceNode) {
        super(affectedPlaceNode);
    }

}
