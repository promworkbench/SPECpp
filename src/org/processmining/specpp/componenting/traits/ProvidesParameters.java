package org.processmining.specpp.componenting.traits;

import org.processmining.specpp.componenting.data.DataRequirement;
import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.data.ParameterSourceCollection;
import org.processmining.specpp.componenting.system.ComponentType;

public interface ProvidesParameters extends UsesGlobalComponentSystem {

    default ParameterSourceCollection parameters() {
        return (ParameterSourceCollection) getComponentCollection().<DataRequirement<?>>getProvisions(ComponentType.Parameters);
    }

}
