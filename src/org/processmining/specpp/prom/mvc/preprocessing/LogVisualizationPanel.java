package org.processmining.specpp.prom.mvc.preprocessing;


import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.specpp.prom.mvc.error.MessagePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class LogVisualizationPanel extends JPanel {

    private final ProMVisualizerAdapter proMVisualizerAdapter;
    private final XLog rawLog;

    public LogVisualizationPanel(ProMVisualizerAdapter proMVisualizerAdapter, XLog rawLog) {
        super(new BorderLayout());
        this.proMVisualizerAdapter = proMVisualizerAdapter;
        this.rawLog = rawLog;
        add(new MessagePanel("loading visualization..."));
        VisualizationWorker w = new VisualizationWorker();
        w.run();
    }

    public static LogVisualizationPanel fromContext(UIPluginContext context, XLog rawLog) {
        List<ProMVisualizerAdapter> list = ProMVisualizerAdapter.getAvailableVisualizers(context, rawLog);
        Predicate<String> predicate = Pattern.compile("Explore.+Event.+Log.+Trace.+Variants").asPredicate();
        Optional<ProMVisualizerAdapter> first = list.stream()
                                                    .filter(pva -> predicate.test(pva.getDisplayName()))
                                                    .findFirst();
        return new LogVisualizationPanel(first.orElse(null), rawLog);
    }

    private class VisualizationWorker extends SwingWorker<JComponent, Void> {


        @Override
        protected JComponent doInBackground() throws Exception {
            return proMVisualizerAdapter == null ? null : proMVisualizerAdapter.callPlugin(rawLog);
        }

        @Override
        protected void done() {
            try {
                JComponent jComponent = get();
                add(jComponent, BorderLayout.CENTER);
            } catch (InterruptedException | ExecutionException e) {
                add(new MessagePanel(e.getMessage()), BorderLayout.CENTER);
            }
        }
    }


}
