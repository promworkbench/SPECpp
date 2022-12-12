package org.processmining.specpp.config.components;

import org.processmining.specpp.componenting.system.ComponentInitializer;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;

public interface ComponentInitializerBuilder<T extends ComponentInitializer> extends InitializingBuilder<T, GlobalComponentRepository> {
}
