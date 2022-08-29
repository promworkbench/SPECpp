package org.processmining.specpp.datastructures.tree.heuristic;

import org.processmining.specpp.traits.ZeroOneBounded;

public class PlaceNodeHeuristic extends DoubleScore implements ZeroOneBounded {

    public PlaceNodeHeuristic(double score) {
        super(score);
    }


}
