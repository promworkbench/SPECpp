package org.processmining.specpp.componenting.system;

import org.processmining.specpp.componenting.traits.*;

public class GlobalComponentRepository extends ComponentCollection implements UsesGlobalComponentSystem, ProvidesDataSources, ProvidesEvaluators, ProvidesSupervisors, ProvidesParameters {

    public GlobalComponentRepository() {
        addComponent(ComponentType.Data);
        addComponent(ComponentType.Evaluation);
        addComponent(ComponentType.Supervision);
        addComponent(ComponentType.Parameters);
    }

    @Override
    public ComponentCollection globalComponentSystem() {
        return this;
    }

}
