package org.processmining.specpp.supervision.instrumentators;

import org.processmining.specpp.componenting.delegators.AbstractFCSUDelegator;
import org.processmining.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.specpp.supervision.piping.TimeStopper;

public class AbstractInstrumentingDelegator<T extends FullComponentSystemUser> extends AbstractFCSUDelegator<T> {


    protected final TimeStopper timeStopper = new TimeStopper();

    public AbstractInstrumentingDelegator(T delegate) {
        super(delegate);
    }

}
