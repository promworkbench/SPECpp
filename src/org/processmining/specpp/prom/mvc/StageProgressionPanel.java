package org.processmining.specpp.prom.mvc;

import org.processmining.specpp.prom.util.Iconic;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StageProgressionPanel extends JPanel {

    private final SPECppController parentController;
    private SPECppController.PluginStage currentStage;

    private final ArrayList<JButton> stageButtons;

    public StageProgressionPanel(SPECppController parentController) {
        super(new GridBagLayout());
        this.parentController = parentController;

        setBorder(BorderFactory.createRaisedBevelBorder());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.NONE;

        stageButtons = new ArrayList<>();
        for (SPECppController.PluginStage stage : SPECppController.PLUGIN_STAGES) {
            JButton jButton = new JButton(stage.toString());
            jButton.setContentAreaFilled(false);
            jButton.setHorizontalTextPosition(JButton.CENTER);
            jButton.setVerticalTextPosition(JButton.CENTER);
            jButton.setBorder(BorderFactory.createEmptyBorder());
            jButton.setOpaque(false);
            locked(jButton);
            jButton.setMinimumSize(new Dimension(150, 50));
            //jButton.setPreferredSize(new Dimension(150, 50));
            jButton.addActionListener(e -> {
                if (stage != currentStage) parentController.setPluginStage(stage);
            });
            stageButtons.add(jButton);
            c.gridy = 0;
            add(jButton, c);
        }

    }

    private void highlighted(JButton button) {
        button.setIcon(Iconic.chevron_pink);
        button.setEnabled(true);
    }

    private void unlocked(JButton button) {
        button.setIcon(Iconic.chevron_blue);
        button.setEnabled(true);
    }

    private void locked(JButton button) {
        button.setIcon(Iconic.chevron_white);
        button.setEnabled(false);
    }

    public void updateCurrentStage(SPECppController.PluginStage stage) {
        SwingUtilities.invokeLater(() -> {
            currentStage = stage;
            int ordinal = stage.ordinal();
            highlighted(stageButtons.get(ordinal));
            for (int i = 0; i < ordinal; i++) {
                JButton precedingButton = stageButtons.get(i);
                unlocked(precedingButton);
            }
            for (int i = ordinal + 1; i < stageButtons.size(); i++) {
                JButton postButton = stageButtons.get(i);
                locked(postButton);
            }
        });
    }

    public void unlockStageButton(SPECppController.PluginStage stage) {
        SwingUtilities.invokeLater(() -> unlocked(stageButtons.get(stage.ordinal())));
    }

}
