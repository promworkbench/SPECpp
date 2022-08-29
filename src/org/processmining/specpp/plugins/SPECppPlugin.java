package org.processmining.specpp.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.*;

@Plugin(name = "SPECpp Plugin", parameterLabels = {"Event Log"}, level = PluginLevel.NightlyBuild, quality = PluginQuality.Poor, icon = "specpp_icon.png", returnLabels = {"Discovered Petri net"}, returnTypes = {Object.class}, help = SPECppPlugin.HELP, categories = {PluginCategory.Discovery}, keywords = {"eST", "model discovery"})
public class SPECppPlugin {

    public static final String HELP = "The name is an acronym for Supervised Proposal, Evaluation & Composition + post processing. This plugin provides an extensible framework implementation of the bottom-up Petri net discovery approach also known as eST Miner. it tries to efficiently evaluate all possible place candidates by exploiting token-based fitness monotonicity and guiding the search using heuristics.";

    @UITopiaVariant(affiliation = "PADS RWTH Aachen University", author = "Leah Tacke genannt Unterberg", email = "leah.tgu@pads.rwth-aachen.de")
    @PluginVariant(variantLabel = "Base discovery", requiredParameterLabels = {0})
    public Object run(UIPluginContext context, XLog log) {
        return null;
    }

}