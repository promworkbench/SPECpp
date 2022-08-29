package org.processmining.specpp.supervision.traits;

import org.processmining.specpp.supervision.monitoring.Monitor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Monitoring {

    Collection<Monitor<?, ?>> getMonitors();

    Set<Map.Entry<String, Monitor<?, ?>>> getLabeledMonitor();
}
