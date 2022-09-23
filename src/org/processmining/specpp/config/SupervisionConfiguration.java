package org.processmining.specpp.config;

import com.google.common.collect.ImmutableList;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.supervision.Supervisor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SupervisionConfiguration extends Configuration {

    private final List<SimpleBuilder<Supervisor>> supervisorBuilders;

    public SupervisionConfiguration(GlobalComponentRepository gcr, List<SimpleBuilder<Supervisor>> supervisorBuilders) {
        super(gcr);
        this.supervisorBuilders = supervisorBuilders;
    }

    public List<Supervisor> createSupervisors() {
        return supervisorBuilders.stream().map(this::createFrom).collect(Collectors.toList());
    }


    public static class Configurator implements ComponentInitializerBuilder<SupervisionConfiguration> {

        private final List<SimpleBuilder<Supervisor>> supervisorBuilders;

        public Configurator() {
            supervisorBuilders = new LinkedList<>();
        }

        public Configurator addSupervisor(SimpleBuilder<Supervisor> builder) {
            supervisorBuilders.add(builder);
            return this;
        }

        @Override
        public SupervisionConfiguration build(GlobalComponentRepository gcr) {
            return new SupervisionConfiguration(gcr, ImmutableList.copyOf(supervisorBuilders));
        }
    }

}
