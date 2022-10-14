package org.processmining.specpp.config.parameters;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.composition.composers.DeltaComposerParameters;

public class TauDeltaParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {

    public TauDeltaParameters() {
        globalComponentSystem()
                .provide(ParameterRequirements.DELTA_COMPOSER_PARAMETERS.fulfilWithStatic(DeltaComposerParameters.getDefault()))
                .provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWithStatic(new DeltaParameters(1, 1)));
    }

}
