package org.processmining.specpp.config.components;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.system.ComponentInitializer;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.parameters.SupervisionParameters;

public class Configuration extends ComponentInitializer {
    public Configuration(GlobalComponentRepository gcr) {
        super(gcr);
    }

    public <T> T createFrom(SimpleBuilder<T> builder) {
        return checkout(checkout(builder).build());
    }

    public <T, A> T createFrom(InitializingBuilder<T, A> builder, A argument) {
        return checkout(checkout(builder).build(argument));
    }

    protected boolean shouldBeInstrumented(Object o) {
        SupervisionParameters ask = globalComponentSystem().parameters()
                                                           .askForData(ParameterRequirements.SUPERVISION_PARAMETERS);
        return ask != null && ask.shouldObjBeInstrumented(o);
    }
}
