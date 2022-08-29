package org.processmining.specpp.componenting.system;

import org.processmining.specpp.componenting.traits.UsesGlobalComponentSystem;

public abstract class AbstractGlobalComponentSystemUser implements UsesGlobalComponentSystem {

    private final GlobalComponentRepository componentSystemAdapter;

    public AbstractGlobalComponentSystemUser() {
        componentSystemAdapter = new GlobalComponentRepository();
    }

    protected AbstractGlobalComponentSystemUser(GlobalComponentRepository componentSystemAdapter) {
        this.componentSystemAdapter = componentSystemAdapter;
    }

    @Override
    public GlobalComponentRepository globalComponentSystem() {
        return componentSystemAdapter;
    }

    @Override
    public String toString() {
        return componentSystemAdapter.toString();
    }
}
