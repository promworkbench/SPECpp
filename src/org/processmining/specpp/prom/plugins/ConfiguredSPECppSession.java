package org.processmining.specpp.prom.plugins;

import org.deckfour.xes.model.XLog;

public class ConfiguredSPECppSession extends SPECppSession {

    private final ProMSPECppConfig specppConfig;

    public ConfiguredSPECppSession(XLog eventLog, ProMSPECppConfig specppConfig) {
        super(eventLog);
        this.specppConfig = specppConfig;
    }

    public ProMSPECppConfig getProMSPECppConfig() {
        return specppConfig;
    }
}
