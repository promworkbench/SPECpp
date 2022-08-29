package org.processmining.specpp.supervision;

import org.processmining.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.specpp.componenting.traits.ProvidesSupervisors;
import org.processmining.specpp.traits.Initializable;
import org.processmining.specpp.traits.StartStoppable;

public interface Supervisor extends FullComponentSystemUser, Initializable, StartStoppable, ProvidesSupervisors, IsGlobalProvider {


}
