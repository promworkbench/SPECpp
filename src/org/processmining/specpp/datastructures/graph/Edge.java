package org.processmining.specpp.datastructures.graph;

import org.processmining.specpp.datastructures.util.Pair;

public interface Edge<V extends Vertex> extends GraphObject {

    Pair<V> getVertices();

}
