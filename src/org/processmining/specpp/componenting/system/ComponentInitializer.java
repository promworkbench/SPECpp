package org.processmining.specpp.componenting.system;

import org.processmining.specpp.componenting.traits.*;

public class ComponentInitializer extends AbstractGlobalComponentSystemUser {


    public ComponentInitializer(GlobalComponentRepository gcr) {
        super(gcr);
    }

    public <T> T checkout(T other) {
        if (other instanceof RequiresComponents || other instanceof FulfilledRequirementsCollection || other instanceof ProvisionsComponents) {
            if (other instanceof RequiresComponents) {
                RequiresComponents requiresComponents = (RequiresComponents) other;
                globalComponentSystem().fulfil(requiresComponents);
            }
            if (other instanceof FulfilledRequirementsCollection) {
                FulfilledRequirementsCollection<?> frp = (FulfilledRequirementsCollection<?>) other;
                globalComponentSystem().fulfilFrom(frp);
                if (frp instanceof IsGlobalProvider) globalComponentSystem().absorb(frp);
            }
            if (other instanceof ProvisionsComponents) {
                ProvisionsComponents provisionsComponents = (ProvisionsComponents) other;
                for (FulfilledRequirementsCollection<?> frp : provisionsComponents.componentProvisions().values()) {
                    globalComponentSystem().fulfilFrom(frp);
                    if (frp instanceof IsGlobalProvider) globalComponentSystem().absorb(frp);
                }
            }
        } else if (other instanceof UsesGlobalComponentSystem) {
            checkout(((UsesGlobalComponentSystem) other).globalComponentSystem());
        }
        if (other instanceof IsGlobalProvider) absorbProvisions(other);
        return other;
    }

    public <T> void absorbProvisions(T other) {
        if (other instanceof FulfilledRequirementsCollection) {
            getComponentCollection().absorb((FulfilledRequirementsCollection<?>) other);
        } else if (other instanceof ProvisionsComponents) {
            getComponentCollection().absorb((ProvisionsComponents) other);
        } else if (other instanceof HasComponentCollection) {
            absorbProvisions(((HasComponentCollection) other).getComponentCollection());
        }
    }

    public <T> void overridingAbsorb(T other) {
        if (other instanceof FulfilledRequirementsCollection) {
            getComponentCollection().overridingAbsorb((FulfilledRequirementsCollection<?>) other);
        } else if (other instanceof ProvisionsComponents) {
            getComponentCollection().overridingAbsorb((ProvisionsComponents) other);
        } else if (other instanceof HasComponentCollection) {
            overridingAbsorb(((HasComponentCollection) other).getComponentCollection());
        }
    }

    public <T> T checkoutAndAbsorb(T other) {
        checkout(other);
        absorbProvisions(other);
        return other;
    }

}
