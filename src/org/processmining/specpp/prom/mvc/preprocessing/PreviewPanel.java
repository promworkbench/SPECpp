package org.processmining.specpp.prom.mvc.preprocessing;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.impls.Factory;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class PreviewPanel extends AbstractStagePanel<PreProcessingController> {

    private final DefaultListModel<Activity> postsetListModel;
    private final DefaultListModel<Activity> presetListModel;
    private final ProMList<Activity> presetList;
    private final ProMList<Activity> postsetList;
    private final JButton applyButton;

    public PreviewPanel(PreProcessingController controller) {
        super(controller, new GridBagLayout());
        presetListModel = new DefaultListModel<>();
        postsetListModel = new DefaultListModel<>();

        presetList = new ProMList<>("Preset Activities", presetListModel);
        postsetList = new ProMList<>("Postset Activities", postsetListModel);
        presetList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        postsetList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        presetList.setPreferredSize(new Dimension(200, 200));
        postsetList.setPreferredSize(new Dimension(200, 200));

        applyButton = SlickerFactory.instance().createButton("apply");
        applyButton.addActionListener(e -> {
            controller.applyWorker(collectSelectedActivities());
        });

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.8;
        add(presetList, c);
        c.gridx = 1;
        add(postsetList, c);
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0.2;
        c.gridwidth = 2;
        add(applyButton, c);
    }


    public Pair<Set<Activity>> collectSelectedActivities() {
        // TODO this can be called before the update method below, yielding empty selections.. !
        return new ImmutablePair<>(new HashSet<>(presetList.getSelectedValuesList()), new HashSet<>(postsetList.getSelectedValuesList()));
    }

    public SwingWorker<Pair<List<Activity>>, Void> updateLists(Collection<Activity> activities, Pair<Comparator<Activity>> comparators) {
        SwingWorker<Pair<List<Activity>>, Void> w = new SwingWorker<Pair<List<Activity>>, Void>() {

            @Override
            protected Pair<List<Activity>> doInBackground() throws Exception {
                List<Activity> l1 = new ArrayList<>(activities);
                l1.remove(Factory.ARTIFICIAL_END);
                l1.sort(comparators.first());
                List<Activity> l2 = new ArrayList<>(activities);
                l2.remove(Factory.ARTIFICIAL_START);
                l2.sort(comparators.second());
                return new ImmutablePair<>(l1, l2);
            }

            @Override
            protected void done() {
                try {
                    Pair<List<Activity>> pair = get();
                    presetListModel.clear();
                    postsetListModel.clear();
                    pair.first().forEach(presetListModel::addElement);
                    pair.second().forEach(postsetListModel::addElement);
                    presetList.setSelectedIndices(IntStream.range(0, presetListModel.size()).toArray());
                    postsetList.setSelectedIndices(IntStream.range(0, postsetListModel.size()).toArray());
                } catch (InterruptedException | ExecutionException ignored) {

                } finally {
                    applyButton.setEnabled(true);
                }
            }
        };
        w.execute();
        return w;
    }


    public void disableButton() {
        SwingUtilities.invokeLater(() -> applyButton.setEnabled(false));
    }

    public void enableButton() {
        SwingUtilities.invokeLater(() -> applyButton.setEnabled(true));
    }
}
