package org.processmining.specpp.supervision.instrumentators;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.componenting.system.link.ProposerComponent;
import org.processmining.specpp.supervision.Supervisor;
import org.processmining.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.specpp.supervision.piping.TimeStopper;

import java.util.List;

public class InstrumentedSPECpp<C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> extends SPECpp<C, I, R, F> {

    public static final TaskDescription PEC_CYCLE = new TaskDescription("PEC Cycle");
    public static final TaskDescription TOTAL_CYCLING = new TaskDescription("Total PEC Cycling");

    private final TimeStopper timeStopper = new TimeStopper();


    public InstrumentedSPECpp(GlobalComponentRepository cr, List<Supervisor> supervisors, ProposerComponent<C> proposer, ComposerComponent<C, I, R> composer, PostProcessor<R, F> postProcessor) {
        super(cr, supervisors, proposer, composer, postProcessor);
        globalComponentSystem().provide(SupervisionRequirements.observable("pec.performance", PerformanceEvent.class, timeStopper));
    }

    @Override
    protected void executeAllPECCycles() {
        timeStopper.start(TOTAL_CYCLING);
        super.executeAllPECCycles();
        timeStopper.stop(TOTAL_CYCLING);
    }

    @Override
    public boolean executePECCycle() {
        timeStopper.start(PEC_CYCLE);
        boolean stop = super.executePECCycle();
        timeStopper.stop(PEC_CYCLE);
        return stop;
    }


}
