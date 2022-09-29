package org.processmining.specpp.orchestra;

import org.jfree.chart.ChartPanel;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.PetrinetVisualization;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.util.TypedItem;
import org.processmining.specpp.supervision.monitoring.Monitor;
import org.processmining.specpp.supervision.observations.Visualization;
import org.processmining.specpp.supervision.traits.Monitoring;
import org.processmining.specpp.supervision.traits.ProvidesResults;
import org.processmining.specpp.util.FileUtils;
import org.processmining.specpp.util.PathTools;
import org.processmining.specpp.util.VizUtils;

import javax.swing.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostSpecOps {


    static void postExecution(SPECpp<Place, StatefulPlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specPP, boolean allowPrinting, boolean allowVisualOutput, boolean allowSaving) {
        if (allowPrinting) {
            System.out.println("// ========================================= //");
            System.out.println("Executed " + specPP.currentStepCount() + " ProposalEvaluationComposition cycles.");
        }

        ProMPetrinetWrapper finalResult = specPP.getPostProcessedResult();

        GlobalComponentRepository cr = specPP.getGlobalComponentRepository();
        OutputPathParameters outputPathParameters = cr.parameters()
                                                      .askForData(ParameterRequirements.OUTPUT_PATH_PARAMETERS);

        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.GRAPH, "petri");
        PetrinetVisualization petrinetVisualization = PetrinetVisualization.of(filePath, finalResult);
        if (allowPrinting) printFinalResult(finalResult);
        if (allowVisualOutput) showFinalResult(petrinetVisualization);
        if (allowSaving) saveFinalResult(outputPathParameters, finalResult, petrinetVisualization);
        List<Map.Entry<String, Monitor<?, ?>>> monitors = getMonitors(specPP);
        if (allowPrinting) printMonitoringResults(monitors);
        if (allowVisualOutput) showMonitoringResults(monitors);
        if (allowSaving) saveMonitoringResults(outputPathParameters, monitors);
    }

    private static void saveFinalResult(OutputPathParameters outputPathParameters, ProMPetrinetWrapper finalResult, PetrinetVisualization petrinetVisualization) {
        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.GRAPH, "petri");
        FileUtils.saveDotPanel(filePath, petrinetVisualization.getComponent());
    }

    private static void printFinalResult(ProMPetrinetWrapper finalResult) {
        int edgeCount = finalResult.getNet().getEdges().size();
        int transitionCount = finalResult.getNet().getTransitions().size();
        int placeCount = finalResult.getNet().getPlaces().size();
        System.out.println("The resulting Petri net contains " + placeCount + " places, " + transitionCount + " transitions (incl. artificial start & end) and " + edgeCount + " arcs.");
    }

    private static void showFinalResult(PetrinetVisualization petrinetVisualization) {
        VizUtils.showVisualization(petrinetVisualization);
    }

    private static void printMonitoringResults(List<Map.Entry<String, Monitor<?, ?>>> monitors) {
        for (String resultingString : getResultingStrings(monitors.stream())) {
            System.out.println(resultingString);
        }
    }

    private static void showMonitoringResults(List<Map.Entry<String, Monitor<?, ?>>> monitors) {
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

    static Stream<Map.Entry<String, Monitor<?, ?>>> getMonitorStream(SPECpp<?, ?, ?, ?> specpp) {
        return specpp.getSupervisors()
                     .stream()
                     .filter(s -> s instanceof Monitoring)
                     .map(s -> (Monitoring) s)
                     .flatMap(m -> m.getLabeledMonitor().stream());
    }

    private static List<Map.Entry<String, Monitor<?, ?>>> getMonitors(SPECpp<?, ?, ?, ?> specpp) {
        return getMonitorStream(specpp).collect(Collectors.toList());
    }

    private static List<String> getResultingStrings(Stream<Map.Entry<String, Monitor<?, ?>>> monitors) {
        return monitors.filter(e -> e.getValue() instanceof ProvidesResults)
                       .flatMap(e -> ((ProvidesResults) e.getValue()).getResults()
                                                                     .stream()
                                                                     .map(TypedItem::getItem)
                                                                     .filter(item -> item instanceof String)
                                                                     .map(s -> e.getKey() + ":: " + s))
                       .collect(Collectors.toList());
    }

    private static List<Visualization<?>> getResultingVisualizations(Stream<Map.Entry<String, Monitor<?, ?>>> monitors) {
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
