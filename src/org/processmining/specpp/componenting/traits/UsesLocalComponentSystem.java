package org.processmining.specpp.componenting.traits;

import org.processmining.specpp.componenting.system.ComponentCollection;

/**
 * Trait interface for types which have a local component collection with which they want to connect to the local component system.
 */
public interface UsesLocalComponentSystem extends HasComponentCollection {

    ComponentCollection localComponentSystem();

    @Override
    default ComponentCollection getComponentCollection() {
        return localComponentSystem();
    }

    default void bridgeToChildren() {

    }

}
