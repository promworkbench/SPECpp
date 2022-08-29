package org.processmining.specpp.datastructures.tree.base.traits;

import org.processmining.specpp.datastructures.tree.base.NodeProperties;

public interface HasProperties<P extends NodeProperties> {

    P getProperties();

}
