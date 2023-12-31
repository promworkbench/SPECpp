package org.processmining.specpp.config;

import com.google.common.collect.ImmutableList;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.specpp.config.parameters.Parameters;
import org.processmining.specpp.preprocessing.orderings.*;

import java.util.List;
import java.util.Objects;

public class PreProcessingParameters implements Parameters {

    private final XEventClassifier eventClassifier;
    private final boolean addStartEndTransitions;

    public PreProcessingParameters(XEventClassifier eventClassifier, boolean addStartEndTransitions) {
        this.eventClassifier = eventClassifier;
        this.addStartEndTransitions = addStartEndTransitions;
    }

    public static PreProcessingParameters getDefault() {
        return new PreProcessingParameters(new XEventNameClassifier(), true);
    }

    public static List<Class<? extends ActivityOrderingStrategy>> getAvailableTransitionEncodingsBuilders() {
        return ImmutableList.of(AverageFirstOccurrenceIndex.class, AbsoluteTraceFrequency.class, AbsoluteActivityFrequency.class, Lexicographic.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreProcessingParameters that = (PreProcessingParameters) o;
        return addStartEndTransitions == that.addStartEndTransitions && Objects.equals(eventClassifier, that.eventClassifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventClassifier, addStartEndTransitions);
    }

    @Override
    public String toString() {
        return "PreProcessingParameters{" + "eventClassifierBuilder=" + eventClassifier + ", addStartEndTransitions=" + addStartEndTransitions + '}';
    }

    public boolean isAddStartEndTransitions() {
        return addStartEndTransitions;
    }

    public XEventClassifier getEventClassifier() {
        return eventClassifier;
    }
}
