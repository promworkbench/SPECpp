package org.processmining.specpp.config;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.datastructures.log.ParsedLog;
import org.processmining.specpp.preprocessing.InputDataBundle;

public interface DataExtractionStrategy {

    InputDataBundle extract(ParsedLog parsedLog, DataExtractionParameters parameters);

    default DataSource<InputDataBundle> getExtractor(ParsedLog parsedLog, DataExtractionParameters parameters) {
        return () -> extract(parsedLog, parameters);
    }

    void registerDataSources(GlobalComponentRepository cr, InputDataBundle bundle);

}
