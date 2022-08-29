package org.processmining.specpp.supervision.traits;

import org.processmining.specpp.supervision.observations.Visualization;

import javax.swing.*;

public interface ProvidesOngoingVisualization<V extends JComponent> {

    Visualization<V> getOngoingVisualization();

}
