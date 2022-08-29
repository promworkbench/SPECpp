package org.processmining.specpp.supervision;

import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.supervision.piping.LayingPipe;

public abstract class AbstractSupervisor extends AbstractBaseClass implements Supervisor {

    protected LayingPipe beginLaying() {
        return LayingPipe.inst();
    }

}
