package org.processmining.specpp.datastructures.tree.base.impls;

import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.specpp.componenting.system.link.ExpansionStrategyComponent;
import org.processmining.specpp.datastructures.tree.base.ExpansionStrategy;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.DelayedRooting;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EnumeratingTree<N extends TreeNode & LocallyExpandable<N>> extends AbstractBaseClass implements EfficientTreeComponent<N> {

    protected N root;

    @Override
    public N getRoot() {
        return root;
    }

    @Override
    public void setRootOnce(N root) {
        if (this.root != null) throw new DelayedRooting.Treexecption();
        this.root = root;
        insertNewNode(root);
    }

    private final ExpansionStrategy<N> expansionStrategy;
    protected final Set<N> leaves;
    private N lastExpansion;

    public EnumeratingTree(ExpansionStrategy<N> expansionStrategy) {
        this.expansionStrategy = expansionStrategy;
        this.leaves = new HashSet<>();
        if (expansionStrategy instanceof ExpansionStrategyComponent)
            registerSubComponent(((ExpansionStrategyComponent<N>) expansionStrategy));
    }

    public EnumeratingTree(N root, ExpansionStrategy<N> expansionStrategy) {
        this(expansionStrategy);
        setRootOnce(root);
    }

    @Override
    public Collection<N> getLeaves() {
        return leaves;
    }

    protected final N expandNode(N node) {
        N child = node.generateChild();
        nodeExpanded(node, child);
        insertNewNode(child);
        return child;
    }

    protected void insertNewNode(N node) {
        addLeaf(node);
        expansionStrategy.registerNode(node);
    }

    protected boolean addLeaf(N node) {
        return leaves.add(node);
    }

    protected boolean removeLeaf(N node) {
        return leaves.remove(node);
    }

    protected void softExpand(N child) {
        expansionStrategy.registerPotentialNodes(child.generatePotentialChildren());
    }

    protected N expand() {
        boolean canExpand = false;
        N prospectiveExpansion = null;
        while (expansionStrategy.hasNextExpansion() && !canExpand) {
            prospectiveExpansion = expansionStrategy.nextExpansion();
            canExpand = prospectiveExpansion.canExpand();
            if (!canExpand) lastProposalNotExpandable();
        }
        if (canExpand) return expandNode(prospectiveExpansion);
        else return null;
    }

    protected void lastProposalNotExpandable() {
        notExpandable(expansionStrategy.deregisterPreviousProposal());
    }

    protected void lastExpansionNotExpandable() {
        lastExpansion = null;
        lastProposalNotExpandable();
    }

    protected void notExpandable(N node) {
        removeLeaf(node);
    }

    protected void nodeExpanded(N node, N child) {
        lastExpansion = node;
        if (!node.canExpand()) {
            lastExpansionNotExpandable();
        } else removeLeaf(node);
    }

    @Override
    public N tryExpandingTree() {
        return expand();
    }


    @Override
    public String toString() {
        return "EnumeratingTree(root=" + root + ", lastExpansion=" + lastExpansion + ")";
    }

    @Override
    protected void initSelf() {

    }
}
