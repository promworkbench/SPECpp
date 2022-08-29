package org.processmining.specpp.supervision.observations;

import org.processmining.specpp.traits.ProperlyPrintable;

public class StringObservation implements Observation, ProperlyPrintable {

    private final String text;

    public StringObservation(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
