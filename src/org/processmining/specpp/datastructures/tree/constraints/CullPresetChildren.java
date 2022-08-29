package org.processmining.specpp.datastructures.tree.constraints;

import org.processmining.specpp.datastructures.tree.heuristic.SubtreeCutoffConstraint;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;

public class CullPresetChildren extends SubtreeCutoffConstraint<PlaceNode> {
    public CullPresetChildren(PlaceNode affectedNode) {
        super(affectedNode);
    }
}
