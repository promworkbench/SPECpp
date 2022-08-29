package org.processmining.specpp.config.parameters;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;

public class PlaceFocusParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {
    public PlaceFocusParameters() {
        globalComponentSystem().provide(ParameterRequirements.parameters("placegenerator.parameters", PlaceGeneratorParameters.class, StaticDataSource.of(new PlaceGeneratorParameters(5, true, false, true, true))))
                               .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(new SupervisionParameters(false))));
    }
}
