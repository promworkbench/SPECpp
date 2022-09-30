package org.processmining.specpp.prom.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.*;
import org.processmining.log.utils.XUtils;

@Plugin(name = "Interactive SPECpp Plugin", url = "https://www.pads.rwth-aachen.de/go/id/pnbx/", parameterLabels = {"Event Log", "Config"}, level = PluginLevel.NightlyBuild, quality = PluginQuality.Fair, icon = "specpp_icon.png", returnLabels = {"Interactive SPECpp"}, returnTypes = {SPECppSession.class}, help = SPECppPlugin.HELP, categories = {PluginCategory.Discovery}, keywords = {"eST", "model discovery", "interactive"})
public class InteractiveSPECppPlugin {


    @UITopiaVariant(affiliation = "PADS RWTH Aachen University", author = "Leah Tacke genannt Unterberg", email = "leah.tgu@pads.rwth-aachen.de")
    @PluginVariant(variantLabel = "Interactive SPECpp", requiredParameterLabels = {0})
    public SPECppSession run(UIPluginContext context, XLog log) {
        context.getFutureResult(0).setLabel("Interactive SPECpp on " + XUtils.getConceptName(log));
        return new SPECppSession(log);
    }

    @UITopiaVariant(affiliation = "PADS RWTH Aachen University", author = "Leah Tacke genannt Unterberg", email = "leah.tgu@pads.rwth-aachen.de")
    @PluginVariant(variantLabel = "Interactive SPECpp", requiredParameterLabels = {0, 1})
    public SPECppSession run(UIPluginContext context, XLog log, ProMSPECppConfig config) {
        context.getFutureResult(0).setLabel("Interactive SPECpp on " + XUtils.getConceptName(log));
        return new ConfiguredSPECppSession(log, config);
    }

}
