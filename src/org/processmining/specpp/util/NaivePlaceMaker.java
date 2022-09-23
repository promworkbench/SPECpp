package org.processmining.specpp.util;

import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Transition;

import java.util.Map;
import java.util.stream.Collectors;

public class NaivePlaceMaker extends PlaceMaker {

    private final Map<String, Transition> preLabels;
    private final Map<String, Transition> postLabels;

    public NaivePlaceMaker(IntEncodings<Transition> encodings) {
        super(encodings);
        preLabels = encodings.pre().domain().collect(Collectors.toMap(Transition::toString, t -> t));
        postLabels = encodings.post().domain().collect(Collectors.toMap(Transition::toString, t -> t));
    }

    public class NaiveInProgress extends InProgress {

        public NaiveInProgress preset(String... labels) {
            Transition[] ts = new Transition[labels.length];
            for (int i = 0; i < labels.length; i++) {
                ts[i] = preLabels.get(labels[i]);
            }
            preset(ts);
            return this;
        }

        public NaiveInProgress postset(String... labels) {
            Transition[] ts = new Transition[labels.length];
            for (int i = 0; i < labels.length; i++) {
                ts[i] = postLabels.get(labels[i]);
            }
            postset(ts);
            return this;
        }

    }

    @Override
    public NaiveInProgress start() {
        return new NaiveInProgress();
    }

    public NaiveInProgress preset(String... labels) {
        return start().preset(labels);
    }

}
