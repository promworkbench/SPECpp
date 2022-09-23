package org.processmining.specpp.orchestra;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.preprocessing.InputDataBundle;

public interface SPECppConfigBundle {
    String getTitle();

    String getDescription();

    void instantiate(GlobalComponentRepository cr, InputDataBundle bundle);
}
