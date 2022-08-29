package org.processmining.specpp.config.parameters;

public class PlaceGeneratorParameters implements Parameters {
    @Override
    public String toString() {
        return "PlaceGeneratorParameters{" +
                "maxTreeDepth=" + maxTreeDepth +
                ", acceptWiringConstraints=" + acceptWiringConstraints +
                ", acceptTransitionBlacklistingConstraints=" + acceptTransitionBlacklistingConstraints +
                ", acceptDepthConstraints=" + acceptDepthConstraints +
                ", acceptSubtreeCutoffConstraints=" + acceptSubtreeCutoffConstraints +
                '}';
    }

    private final int maxTreeDepth;
    private final boolean acceptWiringConstraints, acceptTransitionBlacklistingConstraints, acceptDepthConstraints, acceptSubtreeCutoffConstraints;

    public PlaceGeneratorParameters(int maxTreeDepth, boolean acceptSubtreeCutoffConstraints, boolean acceptWiringConstraints, boolean acceptTransitionBlacklistingConstraints, boolean acceptDepthConstraints) {
        this.maxTreeDepth = maxTreeDepth;
        this.acceptSubtreeCutoffConstraints = acceptSubtreeCutoffConstraints;
        this.acceptWiringConstraints = acceptWiringConstraints;
        this.acceptTransitionBlacklistingConstraints = acceptTransitionBlacklistingConstraints;
        this.acceptDepthConstraints = acceptDepthConstraints;
    }

    public static PlaceGeneratorParameters getDefault() {
        return new PlaceGeneratorParameters(Integer.MAX_VALUE, true, false, false, false);
    }

    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }

    public boolean isAcceptWiringConstraints() {
        return acceptWiringConstraints;
    }

    public boolean isAcceptTransitionBlacklistingConstraints() {
        return acceptTransitionBlacklistingConstraints;
    }

    public boolean isAcceptDepthConstraints() {
        return acceptDepthConstraints;
    }

    public boolean isAcceptSubtreeCutoffConstraints() {
        return acceptSubtreeCutoffConstraints;
    }


}
