package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.config.parameters.OutputPathParameters;

public abstract class FileWritingMonitoringSupervisor extends MonitoringSupervisor {

    protected final DelegatingDataSource<OutputPathParameters> pathParametersSource = new DelegatingDataSource<>();

    public FileWritingMonitoringSupervisor() {
        globalComponentSystem().require(ParameterRequirements.OUTPUT_PATH_PARAMETERS, pathParametersSource);
    }

}
