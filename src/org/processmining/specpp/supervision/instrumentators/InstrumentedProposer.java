package org.processmining.specpp.supervision.instrumentators;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.ProposerComponent;
import org.processmining.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;

public class InstrumentedProposer<C extends Candidate> extends AbstractInstrumentingDelegator<ProposerComponent<C>> implements ProposerComponent<C> {
    public static final TaskDescription CANDIDATE_PROPOSAL = new TaskDescription("Candidate Proposal");

    public InstrumentedProposer(ProposerComponent<C> delegate) {
        super(delegate);
        globalComponentSystem().provide(SupervisionRequirements.observable("proposer.performance", PerformanceEvent.class, timeStopper));
    }

    public C proposeCandidate() {
        timeStopper.start(CANDIDATE_PROPOSAL);
        C c = delegate.proposeCandidate();
        timeStopper.stop(CANDIDATE_PROPOSAL);
        return c;
    }

}
