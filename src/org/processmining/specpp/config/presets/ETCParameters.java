package org.processmining.specpp.config.presets;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.config.parameters.ETCBasedComposerParameters;
import org.processmining.specpp.config.parameters.ParameterProvider;

public class ETCParameters extends ParameterProvider {

    public ETCParameters() {
        globalComponentSystem().provide(ParameterRequirements.ETC_BASED_COMPOSER_PARAMETERS.fulfilWithStatic(ETCBasedComposerParameters.getDefault()));
    }

}
