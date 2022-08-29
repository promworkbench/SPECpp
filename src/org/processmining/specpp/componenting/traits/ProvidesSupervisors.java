package org.processmining.specpp.componenting.traits;

import org.processmining.specpp.componenting.supervision.SupervisionRequirement;
import org.processmining.specpp.componenting.system.ComponentType;
import org.processmining.specpp.componenting.system.FulfilledRequirementsCollection;

public interface ProvidesSupervisors extends HasComponentCollection {

    default FulfilledRequirementsCollection<SupervisionRequirement> supervisors() {
        return getComponentCollection().getProvisions(ComponentType.Supervision);
    }

}
