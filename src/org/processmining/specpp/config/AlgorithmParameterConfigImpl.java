package org.processmining.specpp.config;

import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.traits.Copyable;

import java.util.Objects;

public class AlgorithmParameterConfigImpl implements AlgorithmParameterConfig, Copyable<AlgorithmParameterConfigImpl> {

    protected final ProvidesParameters parameterProvider;

    AlgorithmParameterConfigImpl(ProvidesParameters parameterProvider) {
        this.parameterProvider = parameterProvider;
    }

    AlgorithmParameterConfigImpl(ProvidesParameters... parameterProviders) {
        this.parameterProvider = new ParameterProvider() {
            @Override
            public void init() {
                for (ProvidesParameters pp : parameterProviders) {
                    globalComponentSystem().overridingAbsorb(pp);
                }
            }
        };
    }

    @Override
    public String toString() {
        return "AlgorithmParameterConfig{" + parameterProvider + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlgorithmParameterConfigImpl that = (AlgorithmParameterConfigImpl) o;

        return Objects.equals(parameterProvider, that.parameterProvider);
    }

    @Override
    public int hashCode() {
        return parameterProvider != null ? parameterProvider.hashCode() : 0;
    }

    @Override
    public ProvidesParameters getParameters() {
        return parameterProvider;
    }

    public AlgorithmParameterConfigImpl copy() {
        return new AlgorithmParameterConfigImpl(parameterProvider);
    }

}
