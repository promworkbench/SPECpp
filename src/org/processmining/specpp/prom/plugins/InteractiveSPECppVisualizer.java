package org.processmining.specpp.prom.plugins;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.specpp.prom.mvc.SPECppController;

import javax.swing.*;

public class InteractiveSPECppVisualizer {

    @Plugin(name = "@0 Visualize Interactive SPECpp", level = PluginLevel.NightlyBuild, returnLabels = {"Interactive SPECpp"}, returnTypes = {
            JComponent.class}, parameterLabels = {"Interactive SPECpp"})
    @Visualizer
    @PluginVariant(requiredParameterLabels = {0})
    public static JComponent visualize(UIPluginContext context, SPECppSession SPECppSession) {
        return new SPECppController(context, SPECppSession).createPanel();
    }

}
