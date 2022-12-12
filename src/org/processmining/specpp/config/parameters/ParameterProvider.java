package org.processmining.specpp.config.parameters;

import org.processmining.specpp.componenting.data.FulfilledDataRequirement;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;

public abstract class ParameterProvider extends AbstractGlobalComponentSystemUser implements ProvidesParameters {

    public ParameterProvider() {
        init();
    }

    @SafeVarargs
    public static ParameterProvider of(FulfilledDataRequirement<? extends Parameters>... fs) {
        return new ParameterProvider() {
            @Override
            public void init() {
                for (FulfilledDataRequirement<? extends Parameters> f : fs) {
                    globalComponentSystem().provide(f);
                }
            }
        };
    }

    public void init() {
    }

    @Override
    public String toString() {
        return parameters().toString();
    }

}
