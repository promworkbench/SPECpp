package org.processmining.specpp.supervision.observations.performance;

import org.processmining.specpp.datastructures.util.BuilderMap;

public class HeavyPerformanceStatisticBuilder extends BuilderMap<TaskDescription, PerformanceStatistic, PerformanceMeasurement> {

    public HeavyPerformanceStatisticBuilder() {
        super(HeavyPerformanceStatistic::new, PerformanceStatistic::record);
    }

}
