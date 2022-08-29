package org.processmining.specpp.componenting.traits;

import org.processmining.specpp.componenting.data.DataRequirement;
import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.system.ComponentType;

public interface ProvidesParameters extends HasComponentCollection {

    default DataSourceCollection parameters() {
        return (DataSourceCollection) getComponentCollection().<DataRequirement<?>>getProvisions(ComponentType.Parameters);
    }

}
