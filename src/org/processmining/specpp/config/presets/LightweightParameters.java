package org.processmining.specpp.config.presets;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parameters.SupervisionParameters;

public class LightweightParameters extends ParameterProvider {
    public LightweightParameters() {
        globalComponentSystem().provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(SupervisionParameters.instrumentNone(false, false))));
    }
}
