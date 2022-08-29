package org.processmining.specpp.supervision.observations.performance;

import org.processmining.specpp.datastructures.util.BuilderMap;

public class LightweightPerformanceStatisticBuilder extends BuilderMap<TaskDescription, PerformanceStatistic, PerformanceMeasurement> {
    public LightweightPerformanceStatisticBuilder() {
        super(LightweightPerformanceStatistic::new, PerformanceStatistic::record);
    }
}
