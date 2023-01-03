package org.processmining.specpp.config.presets;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.components.Configurators;
import org.processmining.specpp.config.components.SupervisionConfiguration;
import org.processmining.specpp.supervision.supervisors.*;

public class FullSupervisionComponentConfig extends BaseComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository gcr) {
        return Configurators.supervisors()
                            .addSupervisor(BaseSupervisor::new)
                            .addSupervisor(PerformanceSupervisor::new)
                            .addSupervisor(EventCountsSupervisor::new)
                            .addSupervisor(DetailedHeuristicsSupervisor::new)
                            .addSupervisor(DetailedTreeSupervisor::new)
                            .addSupervisor(ProposalTreeSupervisor::new)
                            .addSupervisor(TerminalSupervisor::new)
                            .build(gcr);
    }
}
