package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingObserver;
import org.processmining.specpp.config.parameters.SupervisionParameters;
import org.processmining.specpp.supervision.observations.LogMessage;

public abstract class SubSupervisor extends SchedulingSupervisor {


    protected final DelegatingObserver<LogMessage> fileLogger = new DelegatingObserver<>();
    protected final DelegatingObserver<LogMessage> consoleLogger = new DelegatingObserver<>();

    protected final DelegatingDataSource<SupervisionParameters> supervisionParametersSource = new DelegatingDataSource<>();

    public SubSupervisor() {
        globalComponentSystem().require(BaseSupervisor.FILE_LOGGER_REQUIREMENT, fileLogger)
                               .require(BaseSupervisor.CONSOLE_LOGGER_REQUIREMENT, consoleLogger)
                               .require(ParameterRequirements.SUPERVISION_PARAMETERS, supervisionParametersSource);
    }


    @Override
    public void initSelf() {
        if (globalComponentSystem().areAllRequirementsMet()) instantiateObservationHandlingFullySatisfied();
        else instantiateObservationHandlingPartiallySatisfied();
    }

    protected void instantiateObservationHandlingPartiallySatisfied() {

    }

    protected void instantiateObservationHandlingFullySatisfied() {
        instantiateObservationHandlingPartiallySatisfied();
    }


}
