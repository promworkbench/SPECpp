package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.utils.ProvidedObjectHelper;
import org.processmining.specpp.config.InputProcessingConfig;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.mvc.config.ProMConfig;
import org.processmining.specpp.prom.plugins.ProMSPECppConfig;

import javax.swing.*;

public class ResultExportPanel extends AbstractStagePanel<ResultController> {

    private final PluginContext context;
    private final JButton saveProMPetriButton, saveConfigButton, saveEvalLogButton;

    public ResultExportPanel(ResultController controller) {
        super(controller);
        context = this.controller.getContext();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        saveProMPetriButton = SlickerFactory.instance().createButton("save Petri net to workspace");
        saveProMPetriButton.addActionListener(e -> saveProMPetri());
        add(saveProMPetriButton);
        saveConfigButton = SlickerFactory.instance().createButton("save config to workspace");
        saveConfigButton.addActionListener(e -> saveConfig());
        add(saveConfigButton);

        saveEvalLogButton = SlickerFactory.instance().createButton("save evaluation log to workspace");
        saveEvalLogButton.addActionListener(e -> saveEvalLog());
        if (controller.createEvalLog() == controller.getRawLog()) saveEvalLogButton.setVisible(false);
        add(saveEvalLogButton);
    }

    private void saveProMPetri() {
        ProMPetrinetWrapper result = controller.getResult();
        Petrinet net = result.getNet();
        ProvidedObjectHelper.publish(context, "Petrinet", net, Petrinet.class, true);
        context.getConnectionManager().addConnection(new InitialMarkingConnection(net, result.getInitialMarking()));
        context.getConnectionManager()
               .addConnection(new FinalMarkingConnection(net, result.getFinalMarkings()
                                                                       .stream()
                                                                       .findFirst()
                                                                       .orElse(new Marking())));
        saveProMPetriButton.setEnabled(false);
    }

    private void saveConfig() {
        ProMConfig proMConfig = controller.getParentController().getProMConfig();
        InputProcessingConfig inputProcessingConfig = controller.getParentController().getInputProcessingConfig();
        ProMSPECppConfig config = new ProMSPECppConfig(inputProcessingConfig, proMConfig);
        ProvidedObjectHelper.publish(context, "SPECpp Config", config, ProMSPECppConfig.class, true);
        saveConfigButton.setEnabled(false);
    }

    private void saveEvalLog() {
        XLog evalLog = controller.createEvalLog();
        ProvidedObjectHelper.publish(context, "Evaluation Log", evalLog, XLog.class, true);
        saveEvalLogButton.setEnabled(false);
    }


}
