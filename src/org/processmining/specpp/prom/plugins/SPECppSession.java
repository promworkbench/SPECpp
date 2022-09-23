package org.processmining.specpp.prom.plugins;

import org.deckfour.xes.model.XLog;

public class SPECppSession {
    private final XLog eventLog;

    public SPECppSession(XLog eventLog) {
        this.eventLog = eventLog;
    }

    public XLog getEventLog() {
        return eventLog;
    }
}
