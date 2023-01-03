package org.processmining.specpp.config.presets;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parameters.TreeTrackerParameters;

public class FullSupervisionParameters extends ParameterProvider {

    public FullSupervisionParameters() {
        globalComponentSystem().provide(ParameterRequirements.parameters("tree.tracker.parameters", TreeTrackerParameters.class)
                                                             .fulfilWithStatic(new TreeTrackerParameters(2, 5, 10_000, 100, 300)));
    }
}
