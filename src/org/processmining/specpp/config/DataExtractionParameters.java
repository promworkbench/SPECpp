package org.processmining.specpp.config;

import org.processmining.specpp.config.parameters.Parameters;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingStrategy;
import org.processmining.specpp.preprocessing.orderings.Lexicographic;

import java.util.Objects;

public class DataExtractionParameters implements Parameters {

    private final Class<? extends ActivityOrderingStrategy> activityOrderingStrategy;

    public DataExtractionParameters(Class<? extends ActivityOrderingStrategy> activityOrderingStrategy) {
        this.activityOrderingStrategy = activityOrderingStrategy;
    }

    public static DataExtractionParameters getDefault() {
        return new DataExtractionParameters(Lexicographic.class);
    }

    @Override
    public String toString() {
        return "DataExtractionParameters{" + "activityOrderingStrategy=" + activityOrderingStrategy + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataExtractionParameters that = (DataExtractionParameters) o;

        return Objects.equals(activityOrderingStrategy, that.activityOrderingStrategy);
    }

    @Override
    public int hashCode() {
        return activityOrderingStrategy != null ? activityOrderingStrategy.hashCode() : 0;
    }

    public Class<? extends ActivityOrderingStrategy> getActivityOrderingStrategy() {
        return activityOrderingStrategy;
    }
}
