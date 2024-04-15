package org.processmining.specpp.prom.mvc.discovery;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.prom.computations.ComputationEnded;
import org.processmining.specpp.prom.computations.ComputationEvent;
import org.processmining.specpp.prom.computations.ComputationStarted;
import org.processmining.specpp.prom.computations.OngoingComputation;
import org.processmining.specpp.prom.mvc.swing.LabeledComboBox;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;
import org.processmining.specpp.prom.util.Destructible;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class LiveCompositionPanel extends JPanel implements Destructible {

    private static final int PLACE_LIMIT = 100;
    private final AdvancedComposition<Place> composition;
    private final OngoingComputation ongoingDiscoveryComputation;
    private final JPanel contentPanel;
    private final LabeledComboBox<VisualizationOption> visualizationOptionComboBox;
    private final LivePlacesGraph livePlacesGraph;
    private final LivePlacesList livePlacesList;
    private final LabeledComboBox<Integer> rfInterval;
    private Timer updateTimer;
    private SwingWorker<JComponent, Void> updateWorker;
    private JComponent currentContent;

    public LiveCompositionPanel(AdvancedComposition<Place> composition, OngoingComputation ongoingDiscoveryComputation) {
        super(new GridBagLayout());
        this.composition = composition;
        this.ongoingDiscoveryComputation = ongoingDiscoveryComputation;

        livePlacesGraph = new LivePlacesGraph();
        livePlacesList = new LivePlacesList();

        setBorder(BorderFactory.createRaisedSoftBevelBorder());

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        //header.setOpaque(true);
        //header.setBackground(ColorScheme.lightBlue);
        header.add(SwingFactory.createHeader("Currently Accepted Places"));
        header.add(Box.createHorizontalStrut(150));
        visualizationOptionComboBox = SwingFactory.labeledComboBox("Visualization", VisualizationOption.values());
        visualizationOptionComboBox.getComboBox().setSelectedItem(VisualizationOption.List);
        visualizationOptionComboBox.getComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updateVisualization();
        });

        header.add(visualizationOptionComboBox);

        rfInterval = SwingFactory.labeledComboBox("Refresh Interval [s]", new Integer[]{1, 2, 5, 7, 10});
        rfInterval.getComboBox().setSelectedItem(1);
        rfInterval.getComboBox().addItemListener(e -> {
            if (e.getItem() != null && e.getStateChange() == ItemEvent.SELECTED) setRefreshRate((Integer) e.getItem());
        });
        header.add(rfInterval);

        contentPanel = new JPanel(true);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(Box.createVerticalGlue(), BorderLayout.EAST);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(header, c);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridy++;
        add(contentPanel, c);

        ongoingDiscoveryComputation.addObserver(this::receive);
    }

    public void receive(ComputationEvent e) {
        if (e instanceof ComputationStarted) {
            setRefreshRate((Integer) (rfInterval.getComboBox().getSelectedItem()));
        }
        if (e instanceof ComputationEnded) {
            if (updateTimer != null)
                updateTimer.stop();
            updateVisualization();
        }
    }

    private void setRefreshRate(int interval) {
        if (ongoingDiscoveryComputation.hasEnded()) return;
        int millis = interval * 1000;
        if (updateTimer == null) updateTimer = new Timer(millis, e -> updateVisualization());
        else updateTimer.setDelay(millis);
        updateTimer.restart();
    }

    private void updateVisualization() {
        if (updateWorker != null && !updateWorker.isDone()) updateWorker.cancel(true);
        updateWorker = new SwingWorker<JComponent, Void>() {

            @Override
            protected JComponent doInBackground() throws Exception {
                List<Place> places = composition.toList();


                VisualizationOption option = (VisualizationOption) visualizationOptionComboBox.getComboBox()
                                                                                              .getSelectedItem();
                if (option == null) option = VisualizationOption.List;
                switch (option) {
                    case Graph:
                        if (places.size() > PLACE_LIMIT) return SlickerFactory.instance()
                                                                              .createLabel(String.format("Model is too large to draw: %d places > %d.", places.size(), PLACE_LIMIT));
                        livePlacesGraph.update(places);
                        return livePlacesGraph.getComponent();
                    case List:
                        livePlacesList.update(places);
                        return livePlacesList.getComponent();
                }
                return SlickerFactory.instance().createLabel("Visualization failed.");
            }

            @Override
            protected void done() {
                try {
                    JComponent jComponent = get();
                    if (!isCancelled()) setContent(jComponent);
                } catch (InterruptedException | ExecutionException | CancellationException ignored) {
                    //ignored.printStackTrace();
                }
            }
        };

        updateWorker.execute();
    }

    private void setContent(JComponent jComponent) {
        if (jComponent != currentContent) {
            contentPanel.removeAll();
            contentPanel.add(jComponent, BorderLayout.CENTER);
        }
        currentContent = jComponent;
        currentContent.revalidate();
    }

    @Override
    public void destroy() {
        updateTimer.stop();
    }

    public enum VisualizationOption {
        Graph, List
    }
}
