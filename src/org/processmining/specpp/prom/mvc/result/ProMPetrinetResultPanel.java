package org.processmining.specpp.prom.mvc.result;

import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.discovery.LivePlacesGraph;
import org.processmining.specpp.prom.mvc.error.MessagePanel;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class ProMPetrinetResultPanel extends JPanel {
    public ProMPetrinetResultPanel(ProMPetrinetWrapper proMPetrinetWrapper) {
        super(new BorderLayout());

        new SwingWorker<JComponent, Void>() {

            @Override
            protected JComponent doInBackground() throws Exception {
                if (proMPetrinetWrapper == null) return new MessagePanel("Post Processor failed.");
                LivePlacesGraph lg = new LivePlacesGraph();
                lg.update(proMPetrinetWrapper);
                return lg.getComponent();
            }

            @Override
            protected void done() {
                try {
                    JComponent jComponent = get();
                    add(jComponent);
                    jComponent.revalidate();
                } catch (ExecutionException | InterruptedException e) {
                    add(new MessagePanel("Graph Visualizer failed.\n" + e.getMessage()));
                }
                revalidate();
            }
        }.execute();
    }

}
