package org.processmining.specpp.config;

import org.processmining.specpp.config.parameters.ETCBasedComposerParameters;
import org.processmining.specpp.config.presets.*;

public class ConfigPresets {

    public static final SPECppConfigBundle STANDARD = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new BaseComponentConfig(), new BaseParameters());
    public static final SPECppConfigBundle STANDARD_EXHAUSTIVE = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new BaseComponentConfig(), new BaseParameters(), new ExhaustiveParameters());
    public static final SPECppConfigBundle SUPERVISED_EXHAUSTIVE = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new FullSupervisionComponentConfig(), new BaseParameters(), new ExhaustiveParameters(), new FullSupervisionParameters());
    public static final SPECppConfigBundle STANDARD_LIMITED = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new BaseComponentConfig(), new BaseParameters(), new ExpansionLimitedParameters());
    public static final SPECppConfigBundle LIGHTWEIGHT = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new LightweightComponentConfig(), new BaseParameters(), new LightweightParameters());
    public static final SPECppConfigBundle ETC_Based = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new ETCComponentConfig(), new BaseParameters(), new ETCParameters());
    public static final SPECppConfigBundle TAU_DELTA = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new TauDeltaComponentConfig(), new BaseParameters(), new TauDeltaParameters());
    public static final SPECppConfigBundle UNIWIRED = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new UniwiredComponentConfig(), new BaseParameters(), new UniwiredParameters());
    public static final SPECppConfigBundle PLACE_ORACLE = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new PlaceOracleComponentConfig(), new BaseParameters(), new PlaceOracleParameters());

}
