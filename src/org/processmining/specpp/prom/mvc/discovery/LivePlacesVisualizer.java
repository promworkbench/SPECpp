package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.specpp.datastructures.petri.Place;

import javax.swing.*;
import java.util.List;

public interface LivePlacesVisualizer {


    void update(List<Place> places);

    JComponent getComponent();
}
