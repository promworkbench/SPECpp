package org.processmining.specpp.config;

import org.deckfour.xes.model.XLog;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.datastructures.log.ParsedLog;
import org.processmining.specpp.preprocessing.InputDataBundle;

public interface InputProcessingConfig {

    PreProcessingParameters getPreProcessingParameters();

    PreProcessingStrategy getPreProcessingStrategy();

    DataExtractionParameters getDataExtractionParameters();

    DataExtractionStrategy getDataExtractionStrategy();


    default DataSource<ParsedLog> getParsedLogDataSource(XLog xLog) {
        return getPreProcessingStrategy().getParser(xLog, getPreProcessingParameters());
    }

    default DataSource<InputDataBundle> getInputDataSource(ParsedLog parsedLog) {
        return getDataExtractionStrategy().getExtractor(parsedLog, getDataExtractionParameters());
    }

    default void instantiate(GlobalComponentRepository cr, InputDataBundle inputDataBundle) {
        getDataExtractionStrategy().registerDataSources(cr, inputDataBundle);
    }

}
