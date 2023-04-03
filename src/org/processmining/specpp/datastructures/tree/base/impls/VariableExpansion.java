package org.processmining.specpp.datastructures.tree.base.impls;

import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.componenting.system.link.ExpansionStrategyComponent;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

public class VariableExpansion<N extends TreeNode & LocallyExpandable<N>> extends AbstractBaseClass implements ExpansionStrategyComponent<N> {

    private final Deque<N> buffer;
    private final Supplier<N> peek, dequeue;

    public VariableExpansion() {
        this(true);
    }

    public VariableExpansion(boolean useStack) {
        buffer = new LinkedList<>();
        peek = useStack ? buffer::peekLast : buffer::peekFirst;
        dequeue = useStack ? buffer::removeLast : buffer::removeFirst;
    }

    public static class BFS<N extends TreeNode & LocallyExpandable<N>> extends VariableExpansion<N> {
        public BFS() {
            super(false);
        }
    }

    public static class DFS<N extends TreeNode & LocallyExpandable<N>> extends VariableExpansion<N> {
        public DFS() {
            super(true);
        }
    }

    public static <N extends TreeNode & LocallyExpandable<N>> VariableExpansion<N> dfs() {
        return new VariableExpansion<>(true);
    }

    public static <N extends TreeNode & LocallyExpandable<N>> VariableExpansion<N> bfs() {
        return new VariableExpansion<>(false);
    }

    @Override
    public N nextExpansion() {
        return peek.get();
    }

    @Override
    public boolean hasNextExpansion() {
        return !buffer.isEmpty();
    }

    @Override
    public N deregisterPreviousProposal() {
        return dequeue.get();
    }

    @Override
    public void registerNode(N node) {
        buffer.addLast(node);
    }

    @Override
    public void registerPotentialNodes(Iterable<N> potentialNodes) {

    }

    @Override
    public void deregisterNode(N node) {
        buffer.removeIf(n -> n.equals(node));
    }

    @Override
    protected void initSelf() {

    }
}
