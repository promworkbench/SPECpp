package org.processmining.specpp.supervision.monitoring;

import com.google.common.collect.ImmutableList;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.processmining.specpp.datastructures.util.TypedItem;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.observations.Visualization;
import org.processmining.specpp.supervision.traits.ProvidesOngoingVisualization;
import org.processmining.specpp.supervision.traits.ProvidesResults;
import org.processmining.specpp.util.JavaTypingUtils;

import java.util.Collection;

public interface ChartingMonitor<O extends Observation> extends Monitor<O, JFreeChart>, ProvidesOngoingVisualization<ChartPanel>, ProvidesResults {

    @Override
    default Collection<TypedItem<?>> getResults() {
        return ImmutableList.of(new TypedItem<>(JavaTypingUtils.castClass(Visualization.class), getOngoingVisualization()));
    }
}
