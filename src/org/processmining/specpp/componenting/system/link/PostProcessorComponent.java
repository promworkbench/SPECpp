package org.processmining.specpp.componenting.system.link;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.system.FullComponentSystemUser;

public interface PostProcessorComponent<S extends Result, T extends Result> extends PostProcessor<S, T>, FullComponentSystemUser {
}
