package org.processmining.specpp.datastructures.tree.events;


import org.processmining.specpp.supervision.observations.Event;

public class HeuristicStatsEvent implements Event {

    private final int queueSize;

    public HeuristicStatsEvent(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    @Override
    public String toString() {
        return "HeuristicStats(" + "queueSize=" + queueSize + ")";
    }

}
