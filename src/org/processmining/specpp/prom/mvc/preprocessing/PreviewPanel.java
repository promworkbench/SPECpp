package org.processmining.specpp.prom.mvc.preprocessing;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.impls.Factory;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class PreviewPanel extends AbstractStagePanel<PreProcessingController> {

    private final DefaultListModel<Activity> postsetListModel;
    private final DefaultListModel<Activity> presetListModel;
    private final ProMList<Activity> presetList;
    private final ProMList<Activity> postsetList;

    private Set<Activity> presetSelection, postsetSelection;
    private final JButton applyButton;

    public PreviewPanel(PreProcessingController controller) {
        super(controller, new GridBagLayout());
        presetListModel = new DefaultListModel<>();
        postsetListModel = new DefaultListModel<>();

        presetSelection = new HashSet<>();
        postsetSelection = new HashSet<>();

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
        presetSelection = new HashSet<>(presetList.getSelectedValuesList());
        postsetSelection = new HashSet<>(postsetList.getSelectedValuesList());
        return new ImmutablePair<>(presetSelection, postsetSelection);
    }

    public SwingWorker<Tuple2<Pair<List<Activity>>, Pair<Set<Activity>>>, Void> updateLists(Collection<Activity> activities, Pair<Comparator<Activity>> comparators) {
        SwingWorker<Tuple2<Pair<List<Activity>>, Pair<Set<Activity>>>, Void> w = new SwingWorker<Tuple2<Pair<List<Activity>>, Pair<Set<Activity>>>, Void>() {

            @Override
            protected Tuple2<Pair<List<Activity>>, Pair<Set<Activity>>> doInBackground() throws Exception {
                List<Activity> l1 = new ArrayList<>(activities);
                l1.remove(Factory.ARTIFICIAL_END);
                l1.sort(comparators.first());
                List<Activity> l2 = new ArrayList<>(activities);
                l2.remove(Factory.ARTIFICIAL_START);
                l2.sort(comparators.second());

                Set<Activity> newPresetSelection = new HashSet<>(l1);
                if (newPresetSelection.stream().anyMatch(presetSelection::contains))
                    newPresetSelection.retainAll(presetSelection);

                Set<Activity> newPostsetSelection = new HashSet<>(l2);
                // only try to filter if there is any overlap
                if (newPostsetSelection.stream().anyMatch(postsetSelection::contains))
                    newPostsetSelection.retainAll(postsetSelection);

                presetSelection = newPresetSelection;
                postsetSelection = newPostsetSelection;
                return new ImmutableTuple2<>(new ImmutablePair<>(l1, l2), new ImmutablePair<>(newPresetSelection, newPostsetSelection));
            }

            @Override
            protected void done() {
                try {
                    Tuple2<Pair<List<Activity>>, Pair<Set<Activity>>> tup = get();
                    presetListModel.clear();
                    postsetListModel.clear();
                    Pair<List<Activity>> allActivities = tup.getT1();
                    List<Activity> presetActivityList = allActivities.first();
                    presetActivityList.forEach(presetListModel::addElement);
                    List<Activity> postsetActivityList = allActivities.second();
                    postsetActivityList.forEach(postsetListModel::addElement);
                    Pair<Set<Activity>> selectedActivities = tup.getT2();

                    List<Integer> selectedPresetIndices = new ArrayList<>();
                    List<Integer> selectedPostsetIndices = new ArrayList<>();
                    for (Activity activity : selectedActivities.first()) {
                        int idx = presetActivityList.indexOf(activity);
                        if (idx >= 0) selectedPresetIndices.add(idx);
                    }
                    for (Activity activity : selectedActivities.second()) {
                        int idx = postsetActivityList.indexOf(activity);
                        if (idx >= 0) selectedPostsetIndices.add(idx);
                    }
                    presetList.setSelectedIndices(selectedPresetIndices.stream().mapToInt(i -> i).toArray());
                    postsetList.setSelectedIndices(selectedPostsetIndices.stream().mapToInt(i -> i).toArray());

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
