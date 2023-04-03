package org.processmining.specpp.componenting.system;

import org.processmining.specpp.config.components.SimpleBuilder;

public abstract class ComponentSystemAwareBuilder<T> extends AbstractGlobalComponentSystemUser implements SimpleBuilder<T> {

    protected abstract T buildIfFullySatisfied();

    @Override
    public T build() {
        if (globalComponentSystem().areAllRequirementsMet())
            return buildIfFullySatisfied();
        else return insufficientRequirements();
    }

    protected T insufficientRequirements() {
        throw new RequirementsNotSatisfiedException(globalComponentSystem().toString());
    }

}
