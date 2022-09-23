package org.processmining.specpp.datastructures.tree.heuristic;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.KnowsDepth;

public class HeuristicUtils {

    public static <N extends TreeNode & KnowsDepth & Evaluable> HeuristicStrategy<N, TreeNodeScore> dfs() {
        return n -> new TreeNodeScore(-n.getDepth());
    }

    public static <N extends TreeNode & KnowsDepth & Evaluable> HeuristicStrategy<N, TreeNodeScore> bfs() {
        return n -> new TreeNodeScore(n.getDepth());
    }

}
