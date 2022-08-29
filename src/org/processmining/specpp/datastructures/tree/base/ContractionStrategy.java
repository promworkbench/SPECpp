package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.datastructures.tree.base.traits.LocallyContractible;

public interface ContractionStrategy<N extends TreeNode & LocallyContractible<N>> extends TreeStrategy<N> {

    N nextContraction();

}
