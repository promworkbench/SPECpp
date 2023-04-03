package org.processmining.specpp.componenting.system;

import org.processmining.specpp.componenting.traits.ProvidesDataSources;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.componenting.traits.ProvidesSupervisors;
import org.processmining.specpp.componenting.traits.UsesLocalComponentSystem;

public class LocalComponentRepository extends ComponentCollection implements UsesLocalComponentSystem, ProvidesDataSources, ProvidesEvaluators, ProvidesSupervisors {

    public LocalComponentRepository() {
        addComponent(ComponentType.Data);
        addComponent(ComponentType.Evaluation);
        addComponent(ComponentType.Supervision);
        addComponent(ComponentType.Parameters);
    }

    @Override
    public ComponentCollection localComponentSystem() {
        return this;
    }

}
