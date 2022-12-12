package org.processmining.specpp.preprocessing;

import org.apache.commons.collections4.BidiMap;
import org.deckfour.xes.model.XLog;
import org.processmining.specpp.config.InputProcessingConfig;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.petri.Transition;

public class InputDataBundle {

    private final Log log;
    private final BidiMap<Activity, Transition> mapping;
    private final IntEncodings<Transition> transitionEncodings;

    public InputDataBundle(Log log, IntEncodings<Transition> transitionEncodings, BidiMap<Activity, Transition> mapping) {
        this.log = log;
        this.mapping = mapping;
        this.transitionEncodings = transitionEncodings;
    }

    public Log getLog() {
        return log;
    }

    public BidiMap<Activity, Transition> getMapping() {
        return mapping;
    }

    public IntEncodings<Transition> getTransitionEncodings() {
        return transitionEncodings;
    }

    public static InputDataBundle load(String logPath, InputProcessingConfig inputProcessingConfig) {
        XLog xLog = XLogParser.readLog(logPath);
        return inputProcessingConfig.getInputDataSource(inputProcessingConfig.getParsedLogDataSource(xLog).getData())
                                    .getData();
    }

    @Override
    public String toString() {
        return "InputDataBundle{" + "A=" + mapping.keySet() + ", |A|=" + mapping.size() + " " + "encodings=" + transitionEncodings + "\n" + log + "}";
    }
}
