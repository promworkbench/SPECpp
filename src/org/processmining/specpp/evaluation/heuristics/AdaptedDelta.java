package org.processmining.specpp.evaluation.heuristics;

import org.processmining.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.specpp.traits.ZeroOneBounded;

public class AdaptedDelta extends DoubleScore implements ZeroOneBounded {
    public AdaptedDelta(double score) {
        super(score);
    }
}
