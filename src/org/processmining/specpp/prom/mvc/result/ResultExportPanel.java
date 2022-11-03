package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.utils.ProvidedObjectHelper;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.PreProcessingParameters;
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
        if (controller.getEvalLog() == controller.getRawLog()) saveEvalLogButton.setVisible(false);
        add(saveEvalLogButton);
    }

    private void saveProMPetri() {
        ProMPetrinetWrapper result = controller.getResult();
        context.getProvidedObjectManager().createProvidedObject("Petrinet", result.getNet(), Petrinet.class, context);
        context.getConnectionManager().addConnection(new InitialMarkingConnection(result, result.getInitialMarking()));
        context.getConnectionManager()
               .addConnection(new FinalMarkingConnection(result, result.getFinalMarkings()
                                                                       .stream()
                                                                       .findFirst()
                                                                       .orElse(new Marking())));
        ProvidedObjectHelper.setFavorite(context, result);
        saveProMPetriButton.setEnabled(false);
    }

    private void saveConfig() {
        ProMConfig proMConfig = controller.getParentController().getProMConfig();
        PreProcessingParameters preProcessingParameters = controller.getParentController().getPreProcessingParameters();
        ProMSPECppConfig config = new ProMSPECppConfig(preProcessingParameters, proMConfig);
        context.getProvidedObjectManager().createProvidedObject("Config", config, ProMSPECppConfig.class, context);
        ProvidedObjectHelper.setFavorite(context, config);
        saveConfigButton.setEnabled(false);
    }

    private void saveEvalLog() {
        XLog evalLog = controller.getEvalLog();
        context.getProvidedObjectManager().createProvidedObject("Evaluation Log", evalLog, XLog.class, context);
        ProvidedObjectHelper.setFavorite(context, evalLog);
        saveEvalLogButton.setEnabled(false);
    }


}
