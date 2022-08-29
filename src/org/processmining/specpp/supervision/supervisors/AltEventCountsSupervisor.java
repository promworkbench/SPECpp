package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.supervision.observations.EventCountStatistics;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.supervision.transformers.Transformers;

public class AltEventCountsSupervisor extends EventCountsSupervisor {


    private static final int CAPACITY = 2_000;

    @Override
    protected void layConnections(Observable<?> source, String label) {
        beginLaying().source(source)
                     .giveBackgroundThread()
                     .pipe(PipeWorks.selfEmptyingSummarizingBuffer(Transformers.eventCounter(), CAPACITY))
                     .sinks(PipeWorks.loggingSinks(label + ".count", fileLogger))
                     .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                     .sinks(PipeWorks.loggingSinks(label + ".accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                     .sink(getMonitor(label + ".accumulation"))
                     .apply();
    }
}
