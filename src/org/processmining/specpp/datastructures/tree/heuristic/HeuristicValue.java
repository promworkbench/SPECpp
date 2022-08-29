package org.processmining.specpp.datastructures.tree.heuristic;

import org.processmining.specpp.base.Evaluation;

public interface HeuristicValue<T extends HeuristicValue<T>> extends Evaluation, Comparable<T> {

}
