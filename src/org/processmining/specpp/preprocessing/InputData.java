package org.processmining.specpp.preprocessing;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.util.PublicPaths;

import java.util.function.Function;

public class InputData {


    public static DataSource<InputDataBundle> sampleData() {
        return loadData(PublicPaths.SAMPLE_EVENTLOG_2, PreProcessingParameters.getDefault());
    }

    public static DataSource<InputDataBundle> loadData(String logPath, PreProcessingParameters parameters) {
        return XLogBasedInputDataBundle.fromPath(logPath, parameters);
    }

    public static DataSource<InputDataBundle> createData(Function<PreProcessingParameters, DataSource<InputDataBundle>> dataSourceFunction, PreProcessingParameters parameters) {
        return dataSourceFunction.apply(parameters);
    }

}
