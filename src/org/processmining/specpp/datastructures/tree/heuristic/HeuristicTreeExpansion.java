package org.processmining.specpp.datastructures.tree.heuristic;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.componenting.system.link.ExpansionStrategyComponent;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HeuristicTreeExpansion<N extends TreeNode & Evaluable & LocallyExpandable<N>, H extends HeuristicValue<H>> extends AbstractBaseClass implements ExpansionStrategyComponent<N> {

    protected final PriorityQueue<N> priorityQueue;
    protected final Map<N, H> nodeHeuristics;

    private final HeuristicStrategy<? super N, H> heuristicStrategy;

    public HeuristicTreeExpansion(HeuristicStrategy<? super N, H> heuristicStrategy) {
        this.heuristicStrategy = heuristicStrategy;
        this.nodeHeuristics = new HashMap<>();
        this.priorityQueue = new PriorityQueue<>(Comparator.comparing(nodeHeuristics::get));
    }

    public HeuristicStrategy<? super N, H> getHeuristicStrategy() {
        return heuristicStrategy;
    }

    @Override
    public N nextExpansion() {
        return peekFirst();
    }

    @Override
    public boolean hasNextExpansion() {
        return !priorityQueue.isEmpty();
    }

    @Override
    public N deregisterPreviousProposal() {
        N node = dequeueFirst();
        clearHeuristic(node);
        return node;
    }

    @Override
    public void registerNode(N node) {
        H heuristic = heuristicStrategy.computeHeuristic(node);
        if (nodeHeuristics.containsKey(node)) updateNode(node, heuristic);
        else addNode(node, heuristic);
    }

    @Override
    public void registerPotentialNodes(Iterable<N> potentialNodes) {

    }

    @Override
    public void deregisterNode(N node) {
        removeNode(node);
    }

    protected void addNode(N node, H heuristic) {
        putHeuristic(node, heuristic);
        enqueue(node);
    }

    protected void updateNode(N node, H heuristic) {
        dequeue(node);
        putHeuristic(node, heuristic);
        enqueue(node);
    }

    protected void removeNode(N node) {
        dequeue(node);
        clearHeuristic(node);
    }

    protected void enqueue(N node) {
        priorityQueue.add(node);
    }

    protected void dequeue(N node) {
        priorityQueue.remove(node);
    }

    protected N dequeueFirst() {
        return priorityQueue.poll();
    }

    protected N peekFirst() {
        return priorityQueue.peek();
    }

    protected void putHeuristic(N node, H heuristic) {
        nodeHeuristics.put(node, heuristic);
    }

    protected void clearHeuristic(N node) {
        nodeHeuristics.remove(node);
    }


    @Override
    protected void initSelf() {

    }
}
