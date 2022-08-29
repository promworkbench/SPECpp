package org.processmining.specpp.orchestra;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.Configurators;
import org.processmining.specpp.config.SupervisionConfiguration;
import org.processmining.specpp.supervision.supervisors.*;

public class ExpansiveSPECppComponentConfig extends BaseSPECppComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository gcr) {
        return Configurators.supervisors()
                            .supervisor(BaseSupervisor::new)
                            .supervisor(PerformanceSupervisor::new)
                            .supervisor(EventCountsSupervisor::new)
                            .supervisor(DetailedHeuristicsSupervisor::new)
                            .supervisor(DetailedTreeSupervisor::new)
                            .supervisor(ProposalTreeSupervisor::new)
                            .supervisor(TerminalSupervisor::new)
                            .build(gcr);
    }
}
