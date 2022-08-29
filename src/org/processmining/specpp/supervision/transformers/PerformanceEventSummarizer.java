package org.processmining.specpp.supervision.transformers;

import org.processmining.specpp.datastructures.util.BuilderMap;
import org.processmining.specpp.supervision.observations.performance.*;
import org.processmining.specpp.supervision.piping.ObservationSummarizer;
import org.processmining.specpp.supervision.piping.Observations;

import java.util.function.Supplier;

public class PerformanceEventSummarizer implements ObservationSummarizer<PerformanceEvent, PerformanceStatistics> {

    private final Supplier<BuilderMap<TaskDescription, PerformanceStatistic, PerformanceMeasurement>> collectorSupplier;

    public PerformanceEventSummarizer(Supplier<BuilderMap<TaskDescription, PerformanceStatistic, PerformanceMeasurement>> collectorSupplier) {
        this.collectorSupplier = collectorSupplier;
    }

    @Override
    public PerformanceStatistics summarize(Observations<? extends PerformanceEvent> events) {
        BuilderMap<TaskDescription, PerformanceStatistic, PerformanceMeasurement> collector = collectorSupplier.get();
        for (PerformanceEvent event : events) {
            collector.add(event.getTask(), event.getMeasurement());
        }
        return new PerformanceStatistics(collector.getMap());
    }

}
