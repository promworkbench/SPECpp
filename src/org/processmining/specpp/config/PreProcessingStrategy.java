package org.processmining.specpp.config;

import org.deckfour.xes.model.XLog;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.datastructures.log.ParsedLog;

public interface PreProcessingStrategy {

    ParsedLog parse(XLog xLog, PreProcessingParameters parameters);

    default DataSource<ParsedLog> getParser(XLog xLog, PreProcessingParameters parameters) {
        return () -> parse(xLog, parameters);
    }

}
