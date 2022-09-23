package org.processmining.specpp.prom.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.plugin.annotations.*;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.composition.TrackingPlaceCollection;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.orchestra.ProMSPECppConfigBundle;
import org.processmining.specpp.orchestra.SPECppOperations;
import org.processmining.specpp.preprocessing.XLogBasedInputDataBundle;

@Plugin(name = "SPECpp Plugin", parameterLabels = {"Event Log"}, level = PluginLevel.NightlyBuild, quality = PluginQuality.Poor, icon = "specpp_icon.png", returnLabels = {"Discovered Petri net"}, returnTypes = {Petrinet.class}, help = SPECppPlugin.HELP, categories = {PluginCategory.Discovery}, keywords = {"eST", "model discovery"})
public class SPECppPlugin {

    public static final String HELP = "The name is an acronym for Supervised Proposal, Evaluation & Composition + post processing. This plugin provides an extensible framework implementation of the bottom-up Petri net discovery approach also known as eST Miner. It tries to efficiently evaluate all possible place candidates by exploiting token-based fitness monotonicity and guiding the search using heuristics.";

    @UITopiaVariant(affiliation = "PADS RWTH Aachen University", author = "Leah Tacke genannt Unterberg", email = "leah.tgu@pads.rwth-aachen.de")
    @PluginVariant(variantLabel = "Base discovery", requiredParameterLabels = {0})
    public Petrinet run(UIPluginContext context, XLog log) {

        XLogBasedInputDataBundle dataBundle = XLogBasedInputDataBundle.fromXLog(log, PreProcessingParameters.getDefault());

        SPECpp<Place, TrackingPlaceCollection, PetriNet, ProMPetrinetWrapper> specpp = SPECppOperations.configureAndExecute(ProMSPECppConfigBundle::new, dataBundle, true, false, false);

        ProMPetrinetWrapper pnWrapper = specpp.getPostProcessedResult();

        ConnectionManager connectionManager = context.getConnectionManager();
        Petrinet net = pnWrapper.getNet();
        InitialMarkingConnection initialMarkingConnection = new InitialMarkingConnection(net, pnWrapper.getInitialMarking());
        FinalMarkingConnection finalMarkingConnection = new FinalMarkingConnection(net, pnWrapper.getInitialMarking());
        connectionManager.addConnection(initialMarkingConnection);
        connectionManager.addConnection(finalMarkingConnection);

        return net;
    }


}