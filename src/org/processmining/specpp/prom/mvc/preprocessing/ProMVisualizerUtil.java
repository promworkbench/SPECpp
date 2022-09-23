package org.processmining.specpp.prom.mvc.preprocessing;

import com.google.common.collect.Maps;
import org.deckfour.uitopia.api.model.ViewType;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.contexts.uitopia.hub.ProMResourceManager;
import org.processmining.contexts.uitopia.hub.ProMViewManager;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.impl.PluginManagerImpl;
import org.processmining.framework.util.Pair;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProMVisualizerUtil {

    public static List<ProMVisualizerAdapter> getAvailableVisualizers(UIPluginContext context, XLog inputLog) {
        ProMViewManager vm = ProMViewManager.initialize(context.getGlobalContext());
        ProMResourceManager rm = ProMResourceManager.initialize(context.getGlobalContext());
        List<ViewType> viewTypes = vm.getViewTypes(rm.getResourceForInstance(inputLog));

        Set<Pair<Integer, PluginParameterBinding>> logVisualizers = PluginManagerImpl.getInstance().find(
                Visualizer.class, JComponent.class, context.getPluginContextType(), true, false, false,
                inputLog.getClass());
        logVisualizers.addAll(PluginManagerImpl.getInstance().find(Visualizer.class, JComponent.class,
                context.getPluginContextType(), true, false, false, inputLog.getClass(), ProMCanceller.class));

        Map<String, ProMVisualizerAdapter> proMVisualizerAdapterMap = Maps.newHashMap();
        for (Pair<Integer, PluginParameterBinding> pair : logVisualizers) {
            PluginDescriptor plugin = pair.getSecond().getPlugin();
            String visualizerName = plugin.getAnnotation(Visualizer.class).name();

            if (visualizerName.equals(UITopiaVariant.USEPLUGIN))
                visualizerName = plugin.getAnnotation(Plugin.class).name();
            if (visualizerName.startsWith("@") && visualizerName.contains(" "))
                visualizerName = visualizerName.substring(visualizerName.indexOf(" ") + 1);

            String pluginName = plugin.getAnnotation(Plugin.class).name();

            List<Class<?>> parameterTypes = plugin
                    .getParameterTypes(pair.getSecond().getMethodIndex());

            boolean requiresCanceller = parameterTypes.size() == 2 && parameterTypes.get(1) == ProMCanceller.class;

            proMVisualizerAdapterMap.put(visualizerName, new ProMVisualizerAdapter(context, visualizerName, pluginName, requiresCanceller));
        }

        return viewTypes.stream()
                        .map(vt -> proMVisualizerAdapterMap.get(vt.getTypeName()))
                        .collect(Collectors.toList());
    }

}
