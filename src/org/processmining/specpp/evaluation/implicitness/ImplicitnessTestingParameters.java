package org.processmining.specpp.evaluation.implicitness;

import org.processmining.specpp.config.parameters.Parameters;

public class ImplicitnessTestingParameters implements Parameters {

    private final SubLogRestriction subLogRestriction;
    private final CIPRVersion version;

    public ImplicitnessTestingParameters(CIPRVersion version, SubLogRestriction subLogRestriction) {
        this.version = version;
        this.subLogRestriction = subLogRestriction;
    }

    public SubLogRestriction getSubLogRestriction() {
        return subLogRestriction;
    }

    public static ImplicitnessTestingParameters getDefault() {
        return new ImplicitnessTestingParameters(CIPRVersion.ReplayBased, SubLogRestriction.None);
    }

    public CIPRVersion getVersion() {
        return version;
    }

    public enum CIPRVersion {
        None, ReplayBased, LPBased
    }

    public enum SubLogRestriction {
        None, FittingOnAcceptedPlacesAndEvaluatedPlace, MerelyFittingOnEvaluatedPair
    }

}
