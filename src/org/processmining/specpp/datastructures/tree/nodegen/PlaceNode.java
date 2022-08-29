package org.processmining.specpp.datastructures.tree.nodegen;

import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.base.ChildGenerationLogic;
import org.processmining.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.specpp.datastructures.tree.base.NodeState;
import org.processmining.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;

import java.util.Optional;

/**
 * This class represents a tree node containing a {@code Place} (as {@code NodeProperties}) together with a {@code PlaceState} (as {@code NodeState}) that indicates already generated children nodes.
 * It is an implementation of a {@code GeneratingLocalNode}, that is, it does not hold references to any other nodes and instead employs only its local state and a {@code PlaceGenerator} to compute unseen children.
 *
 * @see Place
 * @see NodeProperties
 * @see PlaceState
 * @see NodeState
 * @see LocalNodeWithExternalizedLogic
 * @see MonotonousPlaceGenerationLogic
 */
public class PlaceNode extends LocalNodeWithExternalizedLogic<Place, PlaceState, PlaceNode> {

    protected PlaceNode(Place place, PlaceState state, ChildGenerationLogic<Place, PlaceState, PlaceNode> generationLogic, boolean isRoot, int depth) {
        super(isRoot, place, state, generationLogic, depth);
    }

    protected static PlaceNode root(Place place, PlaceState state, ChildGenerationLogic<Place, PlaceState, PlaceNode> generationLogic) {
        return new PlaceNode(place, state, generationLogic, true, 0);
    }


    public PlaceNode makeChild(Place childPlace, PlaceState childState) {
        return new PlaceNode(childPlace, childState, getGenerationLogic(), false, getDepth() + 1);
    }

    public Place getPlace() {
        return getProperties();
    }


    @Override
    public Iterable<PlaceNode> generatePotentialChildren() {
        return getGenerationLogic().potentialFutureChildren(this);
    }

    @Override
    public boolean didExpand() {
        return !getState().isCurrentlyALeaf();
    }

    @Override
    protected boolean canExpandBasedOnExternalLogic() {
        return getGenerationLogic().hasChildrenLeft(this);
    }

    @Override
    protected Optional<Boolean> canExpandBasedOnInternalState() {
        return getState().canNeverExpand() ? Optional.of(Boolean.FALSE) : Optional.empty();
    }

    @Override
    public PlaceNode generateChild() {
        return getGenerationLogic().generateChild(this);
    }
}
