package org.processmining.specpp.orchestra;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.preprocessing.InputDataBundle;

public interface DataExtractionConfig {

    void registerDataSources(GlobalComponentRepository cr, InputDataBundle bundle);

}
