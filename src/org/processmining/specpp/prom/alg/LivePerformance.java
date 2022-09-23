package org.processmining.specpp.prom.alg;

import org.processmining.specpp.componenting.delegators.ContainerUtils;
import org.processmining.specpp.supervision.monitoring.PerformanceStatisticsMonitor;
import org.processmining.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.specpp.supervision.observations.performance.PerformanceStatistics;
import org.processmining.specpp.supervision.piping.ConcurrencyBridge;
import org.processmining.specpp.supervision.piping.LayingPipe;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.supervision.supervisors.MonitoringSupervisor;
import org.processmining.specpp.supervision.transformers.Transformers;

import java.time.Duration;

import static org.processmining.specpp.componenting.supervision.SupervisionRequirements.observable;
import static org.processmining.specpp.componenting.supervision.SupervisionRequirements.regex;

public class LivePerformance extends MonitoringSupervisor {

    private final ConcurrencyBridge<PerformanceEvent> performanceEventConcurrencyBridge = PipeWorks.concurrencyBridge();
    private final PerformanceStatisticsMonitor monitor;
    private static final Duration REFRESH_INTERVAL = Duration.ofMillis(100);

    public LivePerformance() {
        globalComponentSystem().require(observable(regex("^.+\\.performance$"), PerformanceEvent.class), ContainerUtils.observeResults(performanceEventConcurrencyBridge));
        monitor = new PerformanceStatisticsMonitor();
        createMonitor("performance", monitor);
    }

    @Override
    protected void instantiateObservationHandlingPartiallySatisfied() {
        LayingPipe lp = beginLaying().source(performanceEventConcurrencyBridge)
                                     .giveBackgroundThread()
                                     .pipe(PipeWorks.summarizingBuffer(Transformers.lightweightPerformanceEventSummarizer()))
                                     .schedule(REFRESH_INTERVAL)
                                     .pipe(PipeWorks.accumulatingPipe(PerformanceStatistics::new));
        if (fileLogger.isSet())
            lp.sink(PipeWorks.loggingSink("performance", PerformanceStatistics::toPrettyString, fileLogger));
        lp.sink(getMonitor("performance")).apply();
    }


}
