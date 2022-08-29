package org.processmining.specpp.base;

import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.datastructures.util.MutableSequentialCollection;
import org.processmining.specpp.traits.IsSizeLimited;

public interface MutableCappedComposition<C extends Candidate> extends CompositionComponent<C>, MutableSequentialCollection<C>, IsSizeLimited {
}
