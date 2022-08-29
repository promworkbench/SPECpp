package org.processmining.specpp.supervision.monitoring;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KeepLastMonitorMap<O extends Observation> implements Monitor<O, Collection<O>> {

    private final Map<Class<? extends Observation>, O> lastSeenMap;

    public KeepLastMonitorMap() {
        lastSeenMap = new HashMap<>();
    }

    @Override
    public Collection<O> getMonitoringState() {
        return lastSeenMap.values();
    }

    @Override
    public void handleObservation(O observation) {
        lastSeenMap.put(observation.getClass(), observation);
    }

}
