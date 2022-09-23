package org.processmining.specpp.datastructures.tree.events;

import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicValue;

public class HeuristicComputationEvent<H extends HeuristicValue<?>> extends TreeNodeEvent<TreeNode> implements TreeHeuristicsEvent {

    private final H heuristic;

    public H getHeuristic() {
        return heuristic;
    }

    public HeuristicComputationEvent(TreeNode node, H heuristic) {
        super(node);
        this.heuristic = heuristic;
    }

    @Override
    public String toString() {
        return "H(" + source + ") = " + heuristic;
    }

}
