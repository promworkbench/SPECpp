package org.processmining.specpp.config.parameters;

/**
 * Parameter to set the threshold to prematurely abort the discovery using the ETC-based composer
 * (if FlagPrematureAbort = true).
 * Threshold used to guide cutting off subtrees (if FlagETCPrecisionCutOff = true).
 */
public class ETCBasedComposerParameters implements Parameters {

    /**
     * Value.
     */
    private final double rho;

    /**
     * Creates a new RhoETCPrecisionThreshold-parameter.
     * @param  p Value for rho.
     * @return RhoETCPrecisionThreshold-parameter.
     */
    public static ETCBasedComposerParameters rho(double p) {
        return new ETCBasedComposerParameters(p);
    }

    /**
    * Returns the RhoETCPrecisionThreshold-parameter with its default value 1.0
    * @return RhoETCPrecisionThreshold-parameter
    */
    public static ETCBasedComposerParameters getDefault() {
        return rho(1.0);
    }

    /**
     * Creates a new RhoETCPrecisionThreshold-parameter.
     * @param p Value for rho.
     */
    public ETCBasedComposerParameters(double p) {
        this.rho = p;
    }

    /**
     * Returns the value of the RhoETCPrecisionThreshold-parameter.
     * @return Rho.
     */
    public double getRho() {
        return rho;
    }

    /**
     * Returns a string with the name of the parameter and its value.
     * @return String.
     */
    @Override
    public String toString() {
        return "ETCPrecisionThreshold(rho=" + rho + ")";
    }
}
