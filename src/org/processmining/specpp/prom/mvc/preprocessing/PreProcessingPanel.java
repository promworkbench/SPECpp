package org.processmining.specpp.prom.mvc.preprocessing;

import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.mvc.swing.TitledBorderPanel;

import java.awt.*;

public class PreProcessingPanel extends AbstractStagePanel<PreProcessingController> {

    public PreProcessingPanel(PreProcessingController controller, VariantPanel variantPanel, ParametersPanel parametersPanel, PreviewPanel previewPanel) {
        super(controller, new GridBagLayout());

        TitledBorderPanel logVariants = new TitledBorderPanel("Previewed Extracted Log Variants", new BorderLayout());
        logVariants.add(variantPanel);
        TitledBorderPanel settings = new TitledBorderPanel("Settings", new BorderLayout());
        settings.add(parametersPanel);
        TitledBorderPanel activitySelection = new TitledBorderPanel("Activity Selection", new BorderLayout());
        activitySelection.add(previewPanel);

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        add(logVariants, c);
        c.gridheight = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx++;
        add(settings, c);
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridy++;
        add(activitySelection, c);

    }


}
