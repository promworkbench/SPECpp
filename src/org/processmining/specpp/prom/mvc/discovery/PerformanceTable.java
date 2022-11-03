package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.framework.util.ui.widgets.ProMScrollPane;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.prom.alg.LivePerformance;
import org.processmining.specpp.prom.computations.ComputationEnded;
import org.processmining.specpp.prom.computations.ComputationEvent;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;
import org.processmining.specpp.prom.util.Destructible;
import org.processmining.specpp.supervision.monitoring.PerformanceStatisticsMonitor;
import org.processmining.specpp.supervision.observations.Statistics;
import org.processmining.specpp.supervision.observations.performance.PerformanceStatistic;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.specpp.supervision.piping.Observer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Duration;
import java.util.Map;

public class PerformanceTable extends JPanel implements Destructible, Observer<ComputationEvent> {

    private final DefaultTableModel model;
    private PerformanceStatisticsMonitor monitor;

    public PerformanceTable(LivePerformance livePerformance) {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Task Description", "avg", "min", "max", "sum", "#measurements", "it/s"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return TaskDescription.class;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        return Duration.class;
                    case 5:
                    case 6:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ProMTable table = SwingFactory.proMTable(model);
        table.setAutoCreateRowSorter(true);
        ProMScrollPane pane = (ProMScrollPane) table.getComponent(1);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        table.getTable().setDefaultRenderer(Duration.class, new DefaultTableCellRenderer() {

            @Override
            protected void setValue(Object value) {
                Duration duration = (Duration) value;
                String s;
                if (duration == null) s = "";
                else if (Duration.ofSeconds(10).compareTo(duration) > 0)
                    s = PerformanceStatistic.durationToString((Duration) value) + "ms";
                else s = duration.toString().substring(2);
                setText(s);
            }
        });
        add(table, BorderLayout.CENTER);

        if (livePerformance != null) {
            monitor = livePerformance.getMonitor("performance", PerformanceStatisticsMonitor.class);
            updateTimer = new Timer(200, e -> updateTable());
            updateTimer.start();
        }
    }

    private Timer updateTimer;


    private void updateTable() {
        Statistics<TaskDescription, PerformanceStatistic> copy = monitor.getMonitoringState().copy();
        model.setRowCount(0);
        for (Map.Entry<TaskDescription, PerformanceStatistic> record : copy.getRecords()) {
            PerformanceStatistic performanceStatistic = record.getValue();
            model.addRow(new Object[]{record.getKey(), performanceStatistic.avg(), performanceStatistic.min(), performanceStatistic.max(), performanceStatistic.sum(), performanceStatistic.N(), performanceStatistic.rate()});
        }
        model.fireTableDataChanged();
    }

    @Override
    public void destroy() {
        if (updateTimer != null) updateTimer.stop();
    }

    @Override
    public void observe(ComputationEvent event) {
        if (event instanceof ComputationEnded && updateTimer != null) updateTimer.stop();
    }
}
