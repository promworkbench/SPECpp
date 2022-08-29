package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.specpp.componenting.system.link.ProposerComponent;
import org.processmining.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

public abstract class AbstractEfficientTreeBasedProposer<C extends Candidate, N extends TreeNode & LocallyExpandable<N>> extends AbstractBaseClass implements ProposerComponent<C> {

    protected final EfficientTree<N> tree;
    private N currentNode;

    public N getPreviousProposedNode() {
        return currentNode;
    }

    protected AbstractEfficientTreeBasedProposer(EfficientTreeComponent<N> tree) {
        this.tree = tree;
        registerSubComponent(tree);
    }

    protected abstract C extractCandidate(N node);

    protected abstract boolean describesValidCandidate(N node);

    @Override
    public C proposeCandidate() {
        currentNode = advance();
        return currentNode != null ? extractCandidate(currentNode) : null;
    }

    protected N advance() {
        N nextNode;
        do {
            nextNode = tree.tryExpandingTree();
        } while (nextNode != null && !describesValidCandidate(nextNode));
        return nextNode;
    }

}
