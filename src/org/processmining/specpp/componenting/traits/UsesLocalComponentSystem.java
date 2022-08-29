package org.processmining.specpp.componenting.traits;

import org.processmining.specpp.componenting.system.ComponentCollection;

public interface UsesLocalComponentSystem extends HasComponentCollection {

    ComponentCollection localComponentSystem();

    @Override
    default ComponentCollection getComponentCollection() {
        return localComponentSystem();
    }

    default void bridgeToChildren() {

    }

}
