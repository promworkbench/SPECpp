package org.processmining.specpp.config.parameters;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;

public class TauDeltaParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {

    public TauDeltaParameters() {
        globalComponentSystem().provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWith(StaticDataSource.of(new DeltaParameters(1))));
    }

}
