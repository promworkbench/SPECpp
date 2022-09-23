package org.processmining.specpp.supervision.observations.performance;

import org.processmining.specpp.supervision.observations.Statistic;
import org.processmining.specpp.supervision.piping.Observer;
import org.processmining.specpp.traits.Mergeable;
import org.processmining.specpp.traits.PrettyPrintable;

import java.text.DecimalFormat;
import java.time.Duration;

public interface PerformanceStatistic extends Statistic, Mergeable<PerformanceStatistic>, Observer<PerformanceMeasurement>, PrettyPrintable {
    DecimalFormat decimalFormat = new DecimalFormat("0.###");

    static String durationToString(Duration duration) {
        return decimalFormat.format((double) duration.toNanos() / 1e6);
    }

    static int calcRate(Duration sum, long n) {
        return (int) (1e3 * n / (double) sum.toMillis());
    }

    void record(PerformanceMeasurement measurement);

    Duration min();

    Duration max();

    Duration avg();

    Duration sum();

    long N();

    @Override
    void merge(PerformanceStatistic other);

    @Override
    String toPrettyString();

    @Override
    default void observe(PerformanceMeasurement observation) {
        record(observation);
    }

    default int rate() {
        return calcRate(sum(), N());
    }
}
