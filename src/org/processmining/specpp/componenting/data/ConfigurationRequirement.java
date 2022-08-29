package org.processmining.specpp.componenting.data;

import org.processmining.specpp.componenting.system.ComponentInitializer;

public class ConfigurationRequirement<F extends ComponentInitializer> extends DataRequirement<F> {

    public ConfigurationRequirement(String label, Class<F> dataType) {
        super(label, dataType);
    }

    @Override
    public String toString() {
        return "ConfigurationRequirement(\"" + label + "\", " + dataType.getSimpleName() + ")";
    }
}
