package org.processmining.specpp.supervision.observations.performance;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.processmining.specpp.supervision.observations.performance.PerformanceStatistic.durationToString;

public class HeavyPerformanceStatistic implements PerformanceStatistic {

    private final List<PerformanceMeasurement> measurements;
    private final SummaryStatistics stats;

    public HeavyPerformanceStatistic() {
        measurements = new ArrayList<>();
        stats = new SummaryStatistics();
    }

    @Override
    public void record(PerformanceMeasurement measurement) {
        measurements.add(measurement);
        stats.addValue(measurement.getDuration().toNanos());
    }

    public SummaryStatistics nanoStatistics() {
        return stats;
    }

    @Override
    public Duration min() {
        return Duration.ofNanos((long) stats.getMin());
    }

    @Override
    public Duration max() {
        return Duration.ofNanos((long) stats.getMax());
    }

    @Override
    public Duration avg() {
        return Duration.ofNanos((long) stats.getMean());
    }

    @Override
    public Duration sum() {
        return Duration.ofNanos((long) stats.getSum());
    }

    public Duration std() {
        return Duration.ofNanos((long) stats.getStandardDeviation());
    }

    @Override
    public long N() {
        return stats.getN();
    }

    @Override
    public String toString() {
        return toPrettyString();
    }

    @Override
    public String toPrettyString() {
        Duration sum = sum();
        long n = N();
        int rate = (int) (1e3 * n / (double) sum.toMillis());
        return "{\u03BC=" + durationToString(avg()) + "ms\u00B1" + durationToString(std()) + "ms" + " (" + durationToString(min()) + "ms-" + durationToString(max()) + "ms), \u03A3=" + sum.toString()
                                                                                                                                                                                           .substring(2) + ", N=" + n + ", " + rate
                + "it/s" + "}";
    }

    @Override
    public void merge(PerformanceStatistic other) {
        if (other instanceof HeavyPerformanceStatistic) {
            for (PerformanceMeasurement m : ((HeavyPerformanceStatistic) other).measurements) {
                record(m);
            }
        }
    }

}
