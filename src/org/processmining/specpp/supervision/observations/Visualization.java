package org.processmining.specpp.supervision.observations;

import javax.swing.*;

public class Visualization<T extends JComponent> implements Observation {

    private final String title;
    private final T component;

    public Visualization(String title, T component) {
        this.title = title;
        this.component = component;
    }

    public String getTitle() {
        return title;
    }

    public T getComponent() {
        return component;
    }
}
