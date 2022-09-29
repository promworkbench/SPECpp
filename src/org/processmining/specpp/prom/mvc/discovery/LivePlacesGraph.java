package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.graphvisualizers.algorithms.GraphVisualizerAlgorithm;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetBuilder;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.error.MessagePanel;

import javax.swing.*;
import java.util.List;

public class LivePlacesGraph implements LivePlacesVisualizer {

    private final int MAX_NODES_TO_VISUALIZE = 150;
    private JComponent jComponent;
    private final GraphVisualizerAlgorithm alg;

    public LivePlacesGraph() {
        alg = new GraphVisualizerAlgorithm();
    }


    @Override
    public void update(List<Place> places) {
        ProMPetrinetBuilder pnb = new ProMPetrinetBuilder(new CollectionOfPlaces(places));
        ProMPetrinetWrapper wrapper = pnb.build();
        update(wrapper);
    }

    @Override
    public JComponent getComponent() {
        return jComponent;
    }

    public void update(ProMPetrinetWrapper petrinet) {
        if (petrinet.getNodes().size() > MAX_NODES_TO_VISUALIZE)
            jComponent = new MessagePanel(String.format("Graph is too large to visualize. (%d nodes)", petrinet.getNodes()
                                                                                                               .size()));
        else jComponent = alg.apply(null, petrinet.getNet());
        //VizUtils.showJComponent("bdf", jComponent, false);
    }
}
