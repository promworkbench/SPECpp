package org.processmining.specpp.componenting.system.link;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Composition;
import org.processmining.specpp.componenting.system.FullComponentSystemUser;

/**
 * A composition that is also a full component system user.
 *
 * @param <C> candidate type
 */
public interface CompositionComponent<C extends Candidate> extends Composition<C>, FullComponentSystemUser {
}
