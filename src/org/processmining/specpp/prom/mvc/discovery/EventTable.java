package org.processmining.specpp.prom.mvc.discovery;

import com.google.common.collect.ImmutableMap;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.composition.events.CandidateAcceptanceRevoked;
import org.processmining.specpp.composition.events.CandidateAccepted;
import org.processmining.specpp.composition.events.CandidateRejected;
import org.processmining.specpp.datastructures.tree.constraints.ClinicallyOverfedPlace;
import org.processmining.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.specpp.datastructures.tree.constraints.CullPostsetChildren;
import org.processmining.specpp.datastructures.tree.constraints.CullPresetChildren;
import org.processmining.specpp.datastructures.tree.events.*;
import org.processmining.specpp.prom.alg.LiveEvents;
import org.processmining.specpp.prom.computations.ComputationEnded;
import org.processmining.specpp.prom.computations.ComputationEvent;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;
import org.processmining.specpp.prom.util.Destructible;
import org.processmining.specpp.supervision.monitoring.KeepLastMonitor;
import org.processmining.specpp.supervision.observations.Event;
import org.processmining.specpp.supervision.observations.*;
import org.processmining.specpp.supervision.piping.Observer;
import org.processmining.specpp.util.JavaTypingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class EventTable extends JPanel implements Destructible, Observer<ComputationEvent> {

    private final DefaultTableModel model;
    private KeepLastMonitor<EventCountStatistics> monitor;
    public static final Map<ClassKey<Event>, String> descriptionDictionary = new ImmutableMap.Builder<ClassKey<Event>, String>().put(new ClassKey<>(ClinicallyUnderfedPlace.class), "A place met the \u25BD(L) >= 1-tau threshold")
                                                                                                                                .put(new ClassKey<>(ClinicallyOverfedPlace.class), "A place met the \u25B3(L) >= 1-tau threshold")
                                                                                                                                .put(new ClassKey<>(CandidateRejected.class), "A place was rejected")
                                                                                                                                .put(new ClassKey<>(CandidateAccepted.class), "A place was accepted")
                                                                                                                                .put(new ClassKey<>(CandidateAcceptanceRevoked.class), "An accepted place was removed again")
                                                                                                                                .put(new ClassKey<>(NodeExpansionEvent.class), "A tree node's child node was generated")
                                                                                                                                .put(new ClassKey<>(NodeExhaustionEvent.class), "A tree node's potential child nodes were exhausted")
                                                                                                                                .put(new ClassKey<>(EnqueuedNodeEvent.class), "A tree node was offered to the heuristically sorted priority queue")
                                                                                                                                .put(new ClassKey<>(DequeuedNodeEvent.class), "A tree node was polled from the heuristically sorted priority queue")
                                                                                                                                .put(new ClassKey<>(LeafAdditionEvent.class), "A tree node was inserted into the candidate tree")
                                                                                                                                .put(new ClassKey<>(LeafRemovalEvent.class), "A tree node is no longer childless inside the candidate tree")
                                                                                                                                .put(new ClassKey<>(HeuristicComputationEvent.class), "Tree node heuristics were computed")
                                                                                                                                .put(new ClassKey<>(CullPresetChildren.class), "A tree nodes preset expansion subtree was cutoff")
                                                                                                                                .put(new ClassKey<>(CullPostsetChildren.class), "A tree nodes postset expansion subtree was cutoff")
                                                                                                                                .build();

    public EventTable(LiveEvents liveEvents) {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Event Class", "Description", "count"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                    case 1:
                        return ClassKey.class;
                    case 2:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }

        };
        ProMTable table = SwingFactory.proMTable(model);
        table.setAutoCreateRowSorter(true);
        add(table, BorderLayout.CENTER);

        if (liveEvents != null) {
            monitor = liveEvents.getMonitor("events", JavaTypingUtils.castClass(KeepLastMonitor.class));
            updateTimer = new Timer(200, e -> updateTable());
            updateTimer.start();
        }
    }

    private Timer updateTimer;

    private void updateTable() {
        Statistics<ClassKey<Event>, Count> copy = monitor.getMonitoringState().copy();
        //TreeSet<Map.Entry<ClassKey<Event>, Count>> entries = new TreeSet<>(Map.Entry.comparingByKey());
        model.setRowCount(0);
        for (Map.Entry<ClassKey<Event>, Count> record : copy.getRecords()) {
            int c = record.getValue().getCount();
            ClassKey<Event> key = record.getKey();
            model.addRow(new Object[]{key, descriptionDictionary.getOrDefault(key, ""), c});
        }
        model.fireTableDataChanged();
    }

    @Override
    public void destroy() {
        if (updateTimer != null) updateTimer.stop();
    }

    @Override
    public void observe(ComputationEvent event) {
        if (event instanceof ComputationEnded) updateTimer.stop();
    }

}
