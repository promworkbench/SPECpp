package org.processmining.specpp.supervision.observations;

import java.util.Objects;

public class DebugEvent implements Event {

    private final String text;

    public DebugEvent(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DebugEvent that = (DebugEvent) o;

        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }
}
