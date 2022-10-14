package org.processmining.specpp.composition.composers;

import org.processmining.specpp.config.parameters.Parameters;

public class DeltaComposerParameters implements Parameters {

    private final int maxQueueSize, artificialTreeDepth;

    public DeltaComposerParameters(int maxQueueSize, int artificialTreeDepth) {
        this.maxQueueSize = maxQueueSize;
        this.artificialTreeDepth = artificialTreeDepth;
    }

    public static DeltaComposerParameters getDefault() {
        return new DeltaComposerParameters(Integer.MAX_VALUE, 0);
    }

    public int getArtificialTreeDepth() {
        return artificialTreeDepth;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }
}
