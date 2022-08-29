package org.processmining.specpp.supervision.traits;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observable;

import java.time.Duration;
import java.util.Map;

public interface Supervisable {

    default Table<String, Class<? extends Observation>, Observable<?>> exposedObservables() {
        return ImmutableTable.of();
    }

    default Map<String, Duration> bufferClearingSchedules() {
        return ImmutableMap.of();
    }

}
