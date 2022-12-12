package org.processmining.specpp.config.presets;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.config.parameters.DeltaComposerParameters;
import org.processmining.specpp.config.parameters.DeltaParameters;
import org.processmining.specpp.config.parameters.ParameterProvider;

public class TauDeltaParameters extends ParameterProvider {

    public TauDeltaParameters() {
        globalComponentSystem()
                .provide(ParameterRequirements.DELTA_COMPOSER_PARAMETERS.fulfilWithStatic(DeltaComposerParameters.getDefault()))
                .provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWithStatic(new DeltaParameters(1, 1)));
    }

}
