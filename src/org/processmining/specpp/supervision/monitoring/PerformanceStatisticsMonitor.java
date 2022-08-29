package org.processmining.specpp.supervision.monitoring;

import org.processmining.specpp.supervision.observations.performance.PerformanceStatistics;

public class PerformanceStatisticsMonitor implements ComputingMonitor<PerformanceStatistics, PerformanceStatistics, String> {

    private PerformanceStatistics last;

    @Override
    public PerformanceStatistics getMonitoringState() {
        return last;
    }

    @Override
    public void handleObservation(PerformanceStatistics observation) {
        last = observation;
    }

    @Override
    public String computeResult() {
        return last.toPrettyString();
    }
}
