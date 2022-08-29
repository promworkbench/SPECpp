package org.processmining.specpp.supervision.monitoring;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.processmining.specpp.supervision.observations.Event;
import org.processmining.specpp.supervision.observations.Visualization;
import org.processmining.specpp.traits.RepresentsChange;

import java.util.function.ToIntFunction;

public class TimeSeriesMonitor<E extends Event> implements ChartingMonitor<E> {

    private final JFreeChart chart;

    public static <E extends RepresentsChange> AccumulatingToIntWrapper<E> delta_accumulator() {
        return new AccumulatingToIntWrapper<>(RepresentsChange::getDelta);
    }

    private final TimeSeries timeSeries;
    private final ToIntFunction<E> mapper;

    private final Visualization<ChartPanel> visualization;

    public TimeSeriesMonitor(String label, ToIntFunction<E> mapper) {
        this.mapper = mapper;
        timeSeries = new TimeSeries(label);
        TimeSeriesCollection ts = new TimeSeriesCollection(timeSeries);
        chart = ChartFactory.createTimeSeriesChart("Timeseries of " + label, "time", label, ts);
        visualization = new Visualization<>("Timeseries of " + label, new ChartPanel(chart, true));
    }

    @Override
    public Visualization<ChartPanel> getOngoingVisualization() {
        return visualization;
    }

    @Override
    public void handleObservation(E observation) {
        int value = mapper.applyAsInt(observation);
        timeSeries.addOrUpdate(new TimeSeriesDataItem(new Millisecond(), value));
    }

    @Override
    public JFreeChart getMonitoringState() {
        return chart;
    }

}
