package org.processmining.specpp.datastructures.tree.base.impls;

import org.processmining.specpp.datastructures.tree.base.LocalNode;
import org.processmining.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.specpp.datastructures.tree.base.NodeState;

public abstract class AbstractLocalNode<P extends NodeProperties, S extends NodeState, N extends AbstractLocalNode<P, S, N>> implements LocalNode<P, S, N> {

    private final boolean isRoot;

    private final P properties;
    private S state;

    private final int depth;
    private final int computedHash;

    public AbstractLocalNode(boolean isRoot, P properties, S initialState, int depth) {
        this.isRoot = isRoot;
        this.properties = properties;
        this.state = initialState;
        this.depth = depth;
        this.computedHash = computeHashCode();
    }

    public P getProperties() {
        return properties;
    }

    @Override
    public S getState() {
        return state;
    }

    public void setState(S state) {
        this.state = state;
    }

    @Override
    public boolean isRoot() {
        return isRoot;
    }

    @Override
    public int getDepth() {
        return depth;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractLocalNode<?, ?, ?> that = (AbstractLocalNode<?, ?, ?>) o;

        if (isRoot != that.isRoot) return false;
        if (depth != that.depth) return false;
        return properties.equals(that.properties);
    }

    private int computeHashCode() {
        int result = (isRoot ? 1 : 0);
        result = 31 * result + properties.hashCode();
        result = 31 * result + depth;
        return result;
    }

    @Override
    public int hashCode() {
        return computedHash;
    }

    @Override
    public String toString() {
        return "(d=" + depth + ", props=" + properties + ", state=" + state + ")";
    }

}
