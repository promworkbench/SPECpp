package org.processmining.specpp.componenting.data;

import org.processmining.specpp.componenting.system.ComponentType;
import org.processmining.specpp.componenting.traits.IsGlobalProvider;

import java.util.stream.Collectors;

public class ParameterSourceCollection extends DataSourceCollection implements IsGlobalProvider {

    @Override
    public ComponentType componentType() {
        return ComponentType.Parameters;
    }

    @Override
    public String toString() {
        return fulfilledRequirements()
                .stream()
                .map(f -> (FulfilledDataRequirement<?>) f)
                .map(f -> "\t" + f.getComparable().toString() + " = " + f.getContent()
                                                                         .getData()
                                                                         .toString())
                .collect(Collectors.joining("\n", "Configured Parameters: {\n", "\n}"));
    }
}
