package org.processmining.specpp.componenting.system.link;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Composer;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.system.FullComponentSystemUser;

public interface ComposerComponent<C extends Candidate, I extends CompositionComponent<C>, R extends Result> extends Composer<C, I, R>, FullComponentSystemUser {
}
