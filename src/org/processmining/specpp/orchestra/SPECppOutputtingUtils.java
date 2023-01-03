package org.processmining.specpp.orchestra;

import org.jfree.chart.ChartPanel;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.SPECppConfigBundle;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.petri.PetrinetVisualization;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.util.TypedItem;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.supervision.monitoring.Monitor;
import org.processmining.specpp.supervision.observations.Visualization;
import org.processmining.specpp.supervision.traits.Monitoring;
import org.processmining.specpp.supervision.traits.ProvidesOngoingVisualization;
import org.processmining.specpp.supervision.traits.ProvidesResults;
import org.processmining.specpp.util.FileUtils;
import org.processmining.specpp.util.PathTools;
import org.processmining.specpp.util.PrintingUtils;
import org.processmining.specpp.util.VizUtils;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Deprecated
public class SPECppOutputtingUtils {


    public static String saveParameters(SPECpp<?, ?, ?, ?> specpp) {
        DataSourceCollection parameters = specpp.getGlobalComponentRepository().parameters();
        String x = PrintingUtils.parametersToPrettyString(parameters);
        OutputPathParameters outputPathParameters = parameters.askForData(ParameterRequirements.OUTPUT_PATH_PARAMETERS);
        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.MISC_EXPORT, "parameters", ".txt");
        FileUtils.saveString(filePath, x);
        return x;
    }

    public static void postSetup(SPECpp<?, ?, ?, ?> specpp, boolean allowPrinting) {
        String x = saveParameters(specpp);
        if (allowPrinting) System.out.println(x);
    }

    public static void preSetup(SPECppConfigBundle configBundle, InputDataBundle data, boolean allowPrinting) {
        if (!allowPrinting) return;
        System.out.println("Log Info");
        Log log = data.getLog();
        int traceCount = log.totalTraceCount();
        int variantCount = log.variantCount();
        Set<Activity> activities = data.getMapping().keySet();
        int activityCount = activities.size();
        System.out.println("Traces: " + traceCount + "\tVariants: " + variantCount + "\t Activities: " + activityCount);
        System.out.println("Top 7 variants:");
        log.stream()
           .sorted(Comparator.comparingInt(ii -> -log.getVariantFrequency(ii.getIndex())))
           .limit(7)
           .forEach(ii -> System.out.println(log.getVariantFrequency(ii.getIndex()) + "\t" + ii.getVariant()));
    }

    public static void duringExecution(SPECpp<?, ?, ?, ?> specpp, boolean allowPrinting) {
        if (allowPrinting) {
            System.out.println("# Commencing SpecOps @" + LocalDateTime.now());
            System.out.println("// ========================================= //");
        }
        for (ProvidesOngoingVisualization<?> ongoingVisualization : getOngoingVisualizations(specpp)) {
            VizUtils.showVisualization(ongoingVisualization.getOngoingVisualization());
        }
        if (allowPrinting) {
            System.out.println("// ========================================= //");
            System.out.println("# Shutting Down SpecOps @" + LocalDateTime.now());
        }
    }


    public static List<ProvidesOngoingVisualization<?>> getOngoingVisualizations(SPECpp<?, ?, ?, ?> specpp) {
        return getMonitorStream(specpp).filter(m -> m instanceof ProvidesOngoingVisualization)
                                       .map(m -> (ProvidesOngoingVisualization<?>) m)
                                       .collect(Collectors.toList());
    }


    public static void postExecution(SPECpp<?, ?, ?, ProMPetrinetWrapper> specpp, boolean allowPrinting, boolean allowVisualOutput, boolean allowSaving) {
        if (allowPrinting) {
            System.out.println("// ========================================= //");
            System.out.println("Executed " + specpp.currentCycleCount() + " ProposalEvaluationComposition (PEC) cycles.");
        }

        ProMPetrinetWrapper finalResult = specpp.getPostProcessedResult();

        GlobalComponentRepository cr = specpp.getGlobalComponentRepository();
        OutputPathParameters outputPathParameters = cr.parameters()
                                                      .askForData(ParameterRequirements.OUTPUT_PATH_PARAMETERS);

        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.GRAPH, "petri");
        PetrinetVisualization petrinetVisualization = PetrinetVisualization.of(filePath, finalResult);
        if (allowPrinting) printFinalResult(finalResult);
        if (allowVisualOutput) showFinalResult(petrinetVisualization);
        if (allowSaving) saveFinalResult(outputPathParameters, finalResult, petrinetVisualization);
        List<Map.Entry<String, Monitor<?, ?>>> monitors = getMonitors(specpp);
        if (allowPrinting) printMonitoringResults(monitors);
        if (allowVisualOutput) showMonitoringResults(monitors);
        if (allowSaving) saveMonitoringResults(outputPathParameters, monitors);
    }

    public static void saveFinalResult(OutputPathParameters outputPathParameters, ProMPetrinetWrapper finalResult, PetrinetVisualization petrinetVisualization) {
        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.GRAPH, "petri");
        FileUtils.saveDotPanel(filePath, petrinetVisualization.getComponent());
    }

    public static void printFinalResult(ProMPetrinetWrapper finalResult) {
        int edgeCount = finalResult.getNet().getEdges().size();
        int transitionCount = finalResult.getNet().getTransitions().size();
        int placeCount = finalResult.getNet().getPlaces().size();
        System.out.println("The resulting Petri net contains " + placeCount + " places, " + transitionCount + " transitions (incl. artificial start & end) and " + edgeCount + " arcs.");
    }

    public static void showFinalResult(PetrinetVisualization petrinetVisualization) {
        VizUtils.showVisualization(petrinetVisualization);
    }

    public static void printMonitoringResults(List<Map.Entry<String, Monitor<?, ?>>> monitors) {
        for (String resultingString : getResultingStrings(monitors.stream())) {
            System.out.println(resultingString);
        }
    }

    public static void showMonitoringResults(List<Map.Entry<String, Monitor<?, ?>>> monitors) {
        for (Visualization<?> resultingVisualization : getResultingVisualizations(monitors.stream())) {
            VizUtils.showVisualization(resultingVisualization);
        }
    }

    public static void saveMonitoringResults(OutputPathParameters outputPathParameters, List<Map.Entry<String, Monitor<?, ?>>> monitors) {
        Collection<CompletableFuture<?>> futures = new LinkedList<>();
        for (Visualization<?> resultingVisualization : getResultingVisualizations(monitors.stream())) {
            JComponent component = resultingVisualization.getComponent();
            String title = resultingVisualization.getTitle().toLowerCase().replace(".", "_");
            if (component instanceof DotPanel) {
                String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.GRAPH, title);
                futures.add(CompletableFuture.runAsync(() -> FileUtils.saveDotPanel(filePath, ((DotPanel) component))));
            } else if (component instanceof ChartPanel) {
                String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.CHART, title);
                futures.add(CompletableFuture.runAsync(() -> FileUtils.saveChart(filePath, ((ChartPanel) component).getChart())));
            }
        }
        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.MISC_EXPORT, "monitoring_results", ".txt");
        FileUtils.saveStrings(filePath, getResultingStrings(monitors.stream()));

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<Map.Entry<String, Monitor<?, ?>>> getMonitorStream(SPECpp<?, ?, ?, ?> specpp) {
        return specpp.getSupervisors()
                     .stream()
                     .filter(s -> s instanceof Monitoring)
                     .map(s -> (Monitoring) s)
                     .flatMap(m -> m.getLabeledMonitor().stream());
    }

    public static List<Map.Entry<String, Monitor<?, ?>>> getMonitors(SPECpp<?, ?, ?, ?> specpp) {
        return getMonitorStream(specpp).collect(Collectors.toList());
    }

    public static List<String> getResultingStrings(Stream<Map.Entry<String, Monitor<?, ?>>> monitors) {
        return monitors.filter(e -> e.getValue() instanceof ProvidesResults)
                       .flatMap(e -> ((ProvidesResults) e.getValue()).getResults()
                                                                     .stream()
                                                                     .map(TypedItem::getItem)
                                                                     .filter(item -> item instanceof String)
                                                                     .map(s -> e.getKey() + ":: " + s))
                       .collect(Collectors.toList());
    }

    public static List<Visualization<?>> getResultingVisualizations(Stream<Map.Entry<String, Monitor<?, ?>>> monitors) {
        return monitors.map(Map.Entry::getValue)
                       .filter(m -> m instanceof ProvidesResults)
                       .map(m -> (ProvidesResults) m)
                       .flatMap(pr -> pr.getResults().stream())
                       .map(TypedItem::getItem)
                       .filter(r -> r instanceof Visualization)
                       .map(r -> (Visualization<?>) r)
                       .collect(Collectors.toList());
    }
}
