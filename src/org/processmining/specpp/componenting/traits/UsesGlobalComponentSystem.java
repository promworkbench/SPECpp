package org.processmining.specpp.componenting.traits;

import org.processmining.specpp.componenting.system.ComponentCollection;

/**
 * Trait interface for types which have a global component collection with which they want to connect to the global component system.
 */
public interface UsesGlobalComponentSystem extends HasComponentCollection, IsGlobalProvider {

    ComponentCollection globalComponentSystem();

    @Override
    default ComponentCollection getComponentCollection() {
        return globalComponentSystem();
    }

}
