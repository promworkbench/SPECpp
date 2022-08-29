package org.processmining.specpp.componenting.traits;

import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.system.ComponentType;

public interface ProvidesDataSources extends HasComponentCollection {

    default DataSourceCollection dataSources() {
        return (DataSourceCollection) (getComponentCollection().getProvisions(ComponentType.Data));
    }

}
