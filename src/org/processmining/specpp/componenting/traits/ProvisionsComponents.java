package org.processmining.specpp.componenting.traits;

import org.processmining.specpp.componenting.delegators.Container;
import org.processmining.specpp.componenting.system.ComponentType;
import org.processmining.specpp.componenting.system.FulfilledRequirementsCollection;
import org.processmining.specpp.componenting.system.Requirement;

import java.util.Map;

public interface ProvisionsComponents {

    Map<ComponentType, FulfilledRequirementsCollection<?>> componentProvisions();

    <C, R extends Requirement<? extends C, R>> void fulfil(R requirement, Container<C> container);

}
