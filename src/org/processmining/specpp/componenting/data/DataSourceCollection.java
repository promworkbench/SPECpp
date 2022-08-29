package org.processmining.specpp.componenting.data;

import org.processmining.specpp.componenting.system.ComponentType;
import org.processmining.specpp.componenting.system.FulfilledRequirement;
import org.processmining.specpp.componenting.system.FulfilledRequirementsCollection;

import java.util.List;

public class DataSourceCollection extends FulfilledRequirementsCollection<DataRequirement<?>> {


    @Override
    public ComponentType componentType() {
        return ComponentType.Data;
    }

    public <T> void register(DataRequirement<T> requirement, DataSource<T> delegate) {
        add(DataRequirements.dataSource(requirement, delegate));
    }

    public <T> void register(FulfilledDataRequirement<T> fulfilledDataRequirement) {
        add(fulfilledDataRequirement);
    }

    public <T> T askForData(DataRequirement<T> requirement) {
        FulfilledRequirement<T, DataRequirement<?>> fulfilledRequirement = satisfyRequirement(requirement);
        return (((FulfilledDataRequirement<T>) fulfilledRequirement)).getContent().getData();
    }

    public void registerAll(List<FulfilledDataRequirement<?>> listOf) {
        for (FulfilledDataRequirement<?> fulfilledDataRequirement : listOf) {
            register(fulfilledDataRequirement);
        }
    }
}
