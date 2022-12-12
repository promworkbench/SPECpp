package org.processmining.specpp.config;

import org.deckfour.xes.model.XLog;
import org.processmining.specpp.datastructures.log.ParsedLog;
import org.processmining.specpp.preprocessing.XLogParser;

public class BasePreProcessingStrategy implements PreProcessingStrategy {
    @Override
    public ParsedLog parse(XLog xLog, PreProcessingParameters parameters) {
        return XLogParser.convertLog(xLog, parameters.getEventClassifier(), parameters.isAddStartEndTransitions());
    }
}
