package org.processmining.specpp.supervision.transformers;

import com.google.common.collect.Lists;
import org.apache.log4j.Level;
import org.processmining.specpp.supervision.observations.LogMessage;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.observations.TimedObservation;
import org.processmining.specpp.supervision.observations.performance.HeavyPerformanceStatisticBuilder;
import org.processmining.specpp.supervision.observations.performance.LightweightPerformanceStatisticBuilder;
import org.processmining.specpp.supervision.piping.ObservationCollection;
import org.processmining.specpp.supervision.piping.ObservationSummarizer;
import org.processmining.specpp.supervision.piping.ObservationTransformer;
import org.processmining.specpp.supervision.piping.Observations;
import org.processmining.specpp.traits.Mergeable;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class Transformers {


    public static <O extends Observation> ObservationTransformer<O, LogMessage> toLogMessage() {
        return o -> new LogMessage(o.getClass().getSimpleName(), o.toString());
    }

    public static <O extends Observation> ObservationTransformer<O, LogMessage> toLogMessage(String source) {
        return o -> new LogMessage(source, o.toString());
    }

    public static <O extends Observation> ObservationTransformer<O, LogMessage> toLogMessage(Level level) {
        return o -> new LogMessage(o.getClass().getSimpleName(), o.toString(), level, LocalDateTime.now());
    }

    public static <O extends Observation> ObservationTransformer<O, O> idTransform() {
        return o -> o;
    }

    public static <O extends Observation> ObservationSummarizer<O, Observations<O>> listCollator() {
        return observations -> new ObservationCollection<>(Lists.newArrayList(observations));
    }

    public static <O extends Observation & Mergeable<? super O>> MergingSummarizer<O> mergingSummarizer() {
        return new MergingSummarizer<>();
    }

    public static <O extends Observation & Mergeable<? super O>> AccumulatingSummarizer<O> accumulatingSummarizer(Supplier<O> initial) {
        return new AccumulatingSummarizer<>(initial);
    }


    public static <O extends Observation & Mergeable<? super O>> AccumulatingTransformer<O> accumulator(Supplier<O> initial) {
        return new AccumulatingTransformer<>(initial);
    }

    public static EventCounter eventCounter() {
        return new EventCounter();
    }

    public static PerformanceEventSummarizer performanceEventSummarizer() {
        return new PerformanceEventSummarizer(HeavyPerformanceStatisticBuilder::new);
    }

    public static PerformanceEventSummarizer lightweightPerformanceEventSummarizer() {
        return new PerformanceEventSummarizer(LightweightPerformanceStatisticBuilder::new);
    }

    public static <O extends Observation> ObservationTransformer<O, TimedObservation<O>> addTime() {
        return o -> new TimedObservation<>(LocalDateTime.now(), o);
    }
}
