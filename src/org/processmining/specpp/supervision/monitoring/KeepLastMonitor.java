package org.processmining.specpp.supervision.monitoring;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.traits.PrettyPrintable;

import java.util.Objects;

public class KeepLastMonitor<O extends Observation> implements ComputingMonitor<O, O, String> {

    private O last;

    @Override
    public String computeResult() {
        return last instanceof PrettyPrintable ? ((PrettyPrintable) last).toPrettyString() : Objects.toString(last);
    }

    @Override
    public O getMonitoringState() {
        return last;
    }

    @Override
    public void handleObservation(O observation) {
        last = observation;
    }
}
