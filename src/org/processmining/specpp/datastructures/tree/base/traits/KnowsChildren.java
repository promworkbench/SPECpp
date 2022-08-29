package org.processmining.specpp.datastructures.tree.base.traits;

import java.util.List;

public interface KnowsChildren<N extends KnowsChildren<N>> {

    List<N> getChildren();

    default boolean isLeaf() {
        return getChildren().isEmpty();
    }

}
