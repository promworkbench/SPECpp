package org.processmining.specpp.componenting.system;

import org.processmining.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.specpp.traits.Initializable;

import java.util.List;

/**
 * Bundles the local & global component system with transitive local component system initialization.
 */
public interface FullComponentSystemUser extends UsesLocalComponentSystem, UsesGlobalComponentSystem, Initializable {

    void registerSubComponent(FullComponentSystemUser subComponent);

    void unregisterSubComponent(FullComponentSystemUser subComponent);

    List<FullComponentSystemUser> collectTransitiveSubcomponents();

    default void connectLocalComponentSystem(LocalComponentRepository lcr) {
        collectTransitiveSubcomponents().forEach(csu -> lcr.consumeEntirely(csu.localComponentSystem()));
        lcr.fulfil(lcr);
    }

    @Override
    default ComponentCollection getComponentCollection() {
        return UsesGlobalComponentSystem.super.getComponentCollection();
    }
}
