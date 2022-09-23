package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicValue;

import java.util.Comparator;

public interface HeuristicStrategy<N extends Evaluable, H extends HeuristicValue<? super H>> extends Evaluator<N, H> {

    H computeHeuristic(N node);

    @Override
    default H eval(N input) {
        return computeHeuristic(input);
    }

    default Comparator<H> heuristicValuesComparator() {
        return Comparator.naturalOrder();
    }

}
