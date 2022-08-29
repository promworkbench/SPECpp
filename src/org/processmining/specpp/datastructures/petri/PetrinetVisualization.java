package org.processmining.specpp.datastructures.petri;

import org.processmining.graphvisualizers.algorithms.GraphVisualizerAlgorithm;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.specpp.supervision.observations.Visualization;

public class PetrinetVisualization extends Visualization<DotPanel> {
    public PetrinetVisualization(String title, DotPanel dotPanel) {
        super(title, dotPanel);
    }

    public static PetrinetVisualization of(ProMPetrinetWrapper petrinetWrapper) {
        return new PetrinetVisualization(petrinetWrapper.getLabel(), visPetrinetWrapper(petrinetWrapper));
    }

    public static PetrinetVisualization of(String title, ProMPetrinetWrapper petrinetWrapper) {
        return new PetrinetVisualization(title, visPetrinetWrapper(petrinetWrapper));
    }

    private static DotPanel visPetrinetWrapper(ProMPetrinetWrapper petrinetWrapper) {
        GraphVisualizerAlgorithm alg = new GraphVisualizerAlgorithm();
        return (DotPanel) alg.apply(null, petrinetWrapper.getNet());
    }
}
