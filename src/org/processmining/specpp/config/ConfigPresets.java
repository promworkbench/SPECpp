package org.processmining.specpp.config;

import org.processmining.specpp.config.parameters.*;
import org.processmining.specpp.config.presets.*;

public class ConfigPresets {

    public static final SPECppConfigBundle STANDARD = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new BaseComponentConfig(), new DefaultParameters());
    public static final SPECppConfigBundle STANDARD_EXHAUSTIVE = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new BaseComponentConfig(), new ExhaustiveParameters());
    public static final SPECppConfigBundle STANDARD_LIMITED = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new BaseComponentConfig(), new ExpansionLimitedParameters());
    public static final SPECppConfigBundle LIGHTWEIGHT = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new LightweightComponentConfig(), new LightweightParameters());
    public static final SPECppConfigBundle TAU_DELTA = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new TauDeltaComponentConfig(), new TauDeltaParameters());
    public static final SPECppConfigBundle UNIWIRED = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new UniwiredComponentConfig(), new UniwiredParameters());
    public static final SPECppConfigBundle PLACE_ORACLE = ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault(), new PlaceOracleComponentConfig(), new PlaceOracleParameters());

}
