package org.processmining.specpp.componenting.traits;

import org.processmining.specpp.componenting.system.ComponentCollection;

public interface UsesGlobalComponentSystem extends HasComponentCollection, IsGlobalProvider {

    ComponentCollection globalComponentSystem();

    @Override
    default ComponentCollection getComponentCollection() {
        return globalComponentSystem();
    }

}
