package org.processmining.specpp.proposal;

import org.processmining.specpp.base.Proposer;
import org.processmining.specpp.base.impls.EfficientTreeWithExternalizedLogicBasedProposer;
import org.processmining.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.base.ConstrainableChildGenerationLogic;
import org.processmining.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;

/**
 * The base implementation of a {@code Proposer} for candidates of type {@code Place}.
 * It internally uses an {@code EnumeratingTree} to deterministically propose all valid place candidates that the underlying tree provides.
 * The tree itself uses an {@code ExpansionStrategy} to determine which nodes to expand next and this class's {@code ConstrainableLocalNodeGenerator} to calculate the child nodes.
 *
 * @see Proposer
 * @see Place
 * @see EnumeratingTree
 * @see ConstrainableChildGenerationLogic
 */
public class PlaceProposer extends EfficientTreeWithExternalizedLogicBasedProposer<Place, PlaceState, PlaceNode> {

    public PlaceProposer(ChildGenerationLogicComponent<Place, PlaceState, PlaceNode> generationLogic, EfficientTreeComponent<PlaceNode> tree) {
        super(generationLogic, tree);
    }

    @Override
    protected boolean describesValidCandidate(PlaceNode node) {
        return node.getPlace().size() >= 2;
    }

    @Override
    protected Place extractCandidate(PlaceNode node) {
        return node.getPlace();
    }

    @Override
    protected void initSelf() {
        PlaceNode root = generationLogic.generateRoot();
        root.setGenerationLogic(generationLogic);
        tree.setRootOnce(root);
    }

}
