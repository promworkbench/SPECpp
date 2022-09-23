package org.processmining.specpp.prom.mvc;

import javax.swing.*;
import java.awt.*;

public class SPECppPanel extends JPanel {

    private final StageProgressionPanel stageProgressionPanel;
    private final SPECppController controller;
    private JPanel mainContentPanel;
    private JPanel mainContent;

    public SPECppPanel(SPECppController controller) {
        super(new GridBagLayout());
        this.controller = controller;

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        stageProgressionPanel = new StageProgressionPanel(controller);
        add(stageProgressionPanel, c);

        mainContentPanel = new JPanel(new BorderLayout());
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 1;
        c.weighty = 1;
        add(mainContentPanel, c);

    }

    public void unlockStage(SPECppController.PluginStage stage) {
        stageProgressionPanel.unlockStageButton(stage);
    }

    public void updatePluginStage(SPECppController.PluginStage stage, JPanel stagePanel) {
        stageProgressionPanel.updateCurrentStage(stage);
        SwingUtilities.invokeLater(() -> {
            if (mainContent != null) mainContentPanel.removeAll();
            mainContent = stagePanel;
            mainContentPanel.add(mainContent, BorderLayout.CENTER);
            revalidate();
        });
    }
}
