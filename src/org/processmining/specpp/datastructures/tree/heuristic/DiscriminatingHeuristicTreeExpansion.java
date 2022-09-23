package org.processmining.specpp.datastructures.tree.heuristic;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.specpp.evaluation.heuristics.TreeHeuristicThreshold;

import java.util.function.Predicate;

public class DiscriminatingHeuristicTreeExpansion<N extends TreeNode & Evaluable & LocallyExpandable<N>, H extends DoubleScore> extends HeuristicTreeExpansion<N, H> {

    protected DelegatingDataSource<TreeHeuristicThreshold> treeHeuristicThresholdSource = new DelegatingDataSource<>();
    private Predicate<H> thresholdPredicate;

    public DiscriminatingHeuristicTreeExpansion(HeuristicStrategy<? super N, H> heuristicStrategy) {
        super(heuristicStrategy);
        globalComponentSystem().require(ParameterRequirements.TREE_HEURISTIC_THRESHOLD, treeHeuristicThresholdSource);
    }

    @Override
    protected void initSelf() {
        thresholdPredicate = treeHeuristicThresholdSource.getData().getPredicate();
    }

    @Override
    protected void addNode(N node, H heuristic) {
        if (meetsThreshold(heuristic)) super.addNode(node, heuristic);
    }

    protected boolean meetsThreshold(H heuristic) {
        return thresholdPredicate.test(heuristic);
    }

}
