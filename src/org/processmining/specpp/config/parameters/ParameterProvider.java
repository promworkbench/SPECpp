package org.processmining.specpp.config.parameters;

import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;

public abstract class ParameterProvider extends AbstractGlobalComponentSystemUser implements ProvidesParameters {

    public ParameterProvider() {
        init();
    }

    public void init(){}

}
