package org.processmining.specpp.componenting.delegators;

import org.processmining.specpp.componenting.system.ComponentCollection;
import org.processmining.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.specpp.componenting.system.LocalComponentRepository;

import java.util.List;

public abstract class AbstractFCSUDelegator<T extends FullComponentSystemUser> extends AbstractDelegator<T> implements FullComponentSystemUser {


    public AbstractFCSUDelegator(T delegate) {
        super(delegate);
    }

    @Override
    public void init() {
        delegate.init();
    }

    public void registerSubComponent(FullComponentSystemUser subComponent) {
        delegate.registerSubComponent(subComponent);
    }

    @Override
    public void unregisterSubComponent(FullComponentSystemUser subComponent) {
        delegate.unregisterSubComponent(subComponent);
    }

    public List<FullComponentSystemUser> collectTransitiveSubcomponents() {
        return delegate.collectTransitiveSubcomponents();
    }

    public void connectLocalComponentSystem(LocalComponentRepository lcr) {
        delegate.connectLocalComponentSystem(lcr);
    }

    public ComponentCollection getComponentCollection() {
        return delegate.getComponentCollection();
    }

    public ComponentCollection globalComponentSystem() {
        return delegate.globalComponentSystem();
    }

    public ComponentCollection localComponentSystem() {
        return delegate.localComponentSystem();
    }

}
