package org.processmining.specpp.supervision.observations.performance;

import java.time.Duration;
import java.util.LongSummaryStatistics;

import static org.processmining.specpp.supervision.observations.performance.PerformanceStatistic.durationToString;

public class LightweightPerformanceStatistic implements PerformanceStatistic {

    private final LongSummaryStatistics nanoStats;


    public LightweightPerformanceStatistic() {
        nanoStats = new LongSummaryStatistics();
    }


    @Override
    public Duration min() {
        return Duration.ofNanos(nanoStats.getMin());
    }

    @Override
    public Duration max() {
        return Duration.ofNanos(nanoStats.getMax());
    }

    @Override
    public Duration avg() {
        return Duration.ofNanos((long) nanoStats.getAverage());
    }

    @Override
    public Duration sum() {
        return Duration.ofNanos(nanoStats.getSum());
    }

    @Override
    public long N() {
        return nanoStats.getCount();
    }

    @Override
    public void merge(PerformanceStatistic other) {
        if (other instanceof LightweightPerformanceStatistic)
            nanoStats.combine(((LightweightPerformanceStatistic) other).nanoStats);
    }

    @Override
    public String toString() {
        return toPrettyString();
    }

    @Override
    public String toPrettyString() {
        Duration sum = sum();
        long n = N();
        int rate = PerformanceStatistic.calcRate(sum, n);
        return "{\u03BC=" + durationToString(avg()) + "ms" + " (" + durationToString(min()) + "ms-" + durationToString(max()) + "ms), \u03A3=" + sum.toString()
                                                                                                                                                    .substring(2) + ", N=" + n + ", " + rate + "it/s" + "}";
    }


    public void record(PerformanceMeasurement performanceMeasurement) {
        nanoStats.accept(performanceMeasurement.getDuration().toNanos());
    }

}
