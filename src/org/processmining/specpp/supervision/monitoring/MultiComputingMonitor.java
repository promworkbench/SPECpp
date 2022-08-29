package org.processmining.specpp.supervision.monitoring;

import org.processmining.specpp.datastructures.util.TypedItem;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.traits.ProvidesResults;

import java.util.Collection;

public interface MultiComputingMonitor<O extends Observation, R> extends Monitor<O, R>, ProvidesResults {

    Collection<TypedItem<?>> computeResults();

    @Override
    default Collection<TypedItem<?>> getResults() {
        return computeResults();
    }
}
