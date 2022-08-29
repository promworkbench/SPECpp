package org.processmining.specpp.supervision.monitoring;

import org.processmining.specpp.supervision.observations.ClassKey;
import org.processmining.specpp.supervision.observations.Observation;

import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.function.ToIntFunction;

public class IntSummaryStatisticsMonitorMap<O extends Observation> implements Monitor<O, Map<ClassKey<? extends Observation>, IntSummaryStatistics>> {

    private final Map<ClassKey<? extends Observation>, IntSummaryStatistics> summaryStatisticsMap;
    private final ToIntFunction<O> intExtractor;

    public IntSummaryStatisticsMonitorMap(ToIntFunction<O> intExtractor) {
        this.intExtractor = intExtractor;
        summaryStatisticsMap = new HashMap<>();
    }

    @Override
    public Map<ClassKey<? extends Observation>, IntSummaryStatistics> getMonitoringState() {
        return summaryStatisticsMap;
    }

    @Override
    public void handleObservation(O observation) {
        ClassKey<? extends Observation> key = new ClassKey<>(observation.getClass());
        if (!summaryStatisticsMap.containsKey(key)) summaryStatisticsMap.put(key, new IntSummaryStatistics());
        summaryStatisticsMap.get(key).accept(intExtractor.applyAsInt(observation));
    }


}
