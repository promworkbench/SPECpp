package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.supervision.ObserverRequirement;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.config.parameters.SupervisionParameters;
import org.processmining.specpp.supervision.AbstractSupervisor;
import org.processmining.specpp.supervision.observations.LogMessage;
import org.processmining.specpp.supervision.piping.Observer;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.PathTools;

public class BaseSupervisor extends AbstractSupervisor {

    public static final ObserverRequirement<LogMessage> FILE_LOGGER_REQUIREMENT = SupervisionRequirements.observer("logger.file", LogMessage.class);
    public static final ObserverRequirement<LogMessage> CONSOLE_LOGGER_REQUIREMENT = SupervisionRequirements.observer("logger.console", LogMessage.class);

    private final DelegatingDataSource<OutputPathParameters> outputPathParameters = new DelegatingDataSource<>();
    private final DelegatingDataSource<SupervisionParameters> supervisionParameters = new DelegatingDataSource<>();

    public BaseSupervisor() {
        globalComponentSystem().require(ParameterRequirements.OUTPUT_PATH_PARAMETERS, outputPathParameters)
                               .require(ParameterRequirements.SUPERVISION_PARAMETERS, supervisionParameters);
    }


    @Override
    public void initSelf() {
        if (supervisionParameters.isSet()) {
            Observer<LogMessage> consoleLogger = o -> {
            };
            SupervisionParameters supervisionParams = supervisionParameters.getData();
            if (supervisionParams.isUseConsole()) consoleLogger = PipeWorks.consoleLogger();

            globalComponentSystem().provide(SupervisionRequirements.observer(CONSOLE_LOGGER_REQUIREMENT, consoleLogger));
            Observer<LogMessage> fileLogger = o -> {
            };
            if (outputPathParameters.isSet() && supervisionParams.isUseUseFiles()) {
                OutputPathParameters parameters = outputPathParameters.getData();
                String filePath = parameters.getFilePath(PathTools.OutputFileType.MAIN_LOG, "main");
                fileLogger = PipeWorks.fileLogger("main", filePath);
            }
            globalComponentSystem().provide(SupervisionRequirements.observer(FILE_LOGGER_REQUIREMENT, fileLogger));
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
