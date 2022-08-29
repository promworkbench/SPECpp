package org.processmining.specpp.componenting.system.link;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Proposer;
import org.processmining.specpp.componenting.system.FullComponentSystemUser;

public interface ProposerComponent<C extends Candidate> extends Proposer<C>, FullComponentSystemUser {
}
