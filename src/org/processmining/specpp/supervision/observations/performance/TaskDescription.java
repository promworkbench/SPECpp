package org.processmining.specpp.supervision.observations.performance;

import org.processmining.specpp.supervision.observations.StringStatisticKey;

public class TaskDescription extends StringStatisticKey {

    public static final TaskDescription HEURISTICS_COMPUTATION = new TaskDescription("Tree Heuristics Computation");
    public static final TaskDescription POST_PROCESSING = new TaskDescription("Post Processing");

    public TaskDescription(String description) {
        super(description);
    }

}
