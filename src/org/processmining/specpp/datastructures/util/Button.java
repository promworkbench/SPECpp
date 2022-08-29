package org.processmining.specpp.datastructures.util;

import org.processmining.specpp.componenting.delegators.DelegatingDataSource;

public class Button extends DelegatingDataSource<Runnable> {

    public void press() {
        if (delegate != null)
            delegate.getData().run();
    }

}
