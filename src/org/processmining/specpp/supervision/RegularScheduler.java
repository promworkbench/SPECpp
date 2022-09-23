package org.processmining.specpp.supervision;

import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.traits.Joinable;
import org.processmining.specpp.traits.StartStoppable;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RegularScheduler implements StartStoppable, Joinable {

    public static final long MAX_WAIT_ON_JOIN = 1000L;
    private final ScheduledExecutorService scheduledExecutorService;
    protected final List<Tuple2<Runnable, Duration>> tasksToSchedule;
    protected final List<ScheduledFuture<?>> schedulersList;

    protected RegularScheduler() {
        tasksToSchedule = new LinkedList<>();
        schedulersList = new LinkedList<>();
        scheduledExecutorService = Executors.newScheduledThreadPool(4);
    }

    public static RegularScheduler inst() {
        return new RegularScheduler();
    }

    public RegularScheduler schedule(Runnable r, Duration timeInterval) {
        if (tasksToSchedule.stream().noneMatch(t -> t.getT1().equals(r)))
            tasksToSchedule.add(new ImmutableTuple2<>(r, timeInterval));
        return this;
    }

    @Override
    public void start() {
        for (Tuple2<Runnable, Duration> tuple : tasksToSchedule) {
            Runnable r = tuple.getT1();
            long millis = tuple.getT2().toMillis();
            ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(r, 0, millis, TimeUnit.MILLISECONDS);
            schedulersList.add(scheduledFuture);
        }
    }

    @Override
    public void stop() {
        schedulersList.forEach(sf -> sf.cancel(false));
        // tasksToSchedule.forEach(p -> scheduledExecutorService.submit(p.getT1()));
        scheduledExecutorService.shutdown();
    }

    @Override
    public void join() throws InterruptedException {
        scheduledExecutorService.awaitTermination(MAX_WAIT_ON_JOIN, TimeUnit.MILLISECONDS);
    }

}
