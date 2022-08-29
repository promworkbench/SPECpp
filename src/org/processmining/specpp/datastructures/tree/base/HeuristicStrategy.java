package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicValue;

@FunctionalInterface
public interface HeuristicStrategy<N extends TreeNode & Evaluable, H extends HeuristicValue<H>> extends Evaluator<N, H> {

    H computeHeuristic(N node);

    @Override
    default H eval(N input) {
        return computeHeuristic(input);
    }

}
