package org.processmining.specpp.config.components;

import org.processmining.specpp.config.parameters.Parameters;

public class ExternalInitializationParameters implements Parameters {

    private final boolean initiallyWireSelfLoops;

    public ExternalInitializationParameters(boolean initiallyWireSelfLoops) {
        this.initiallyWireSelfLoops = initiallyWireSelfLoops;
    }

    public boolean isInitiallyWireSelfLoops() {
        return initiallyWireSelfLoops;
    }

}
