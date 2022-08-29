package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;

public abstract class PlaceGenerationLogic extends AbstractBaseClass implements ConstrainableChildGenerationLogic<Place, PlaceState, PlaceNode, GenerationConstraint>, ChildGenerationLogicComponent<Place, PlaceState, PlaceNode> {

}
