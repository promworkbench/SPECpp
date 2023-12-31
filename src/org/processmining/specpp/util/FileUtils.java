package org.processmining.specpp.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.opencsv.CSVWriter;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.util.Tuple2;

import java.io.*;
import java.util.List;

public class FileUtils {

    public static <T> T readCustomJson(String path, TypeAdapter<T> adapter) {
        try (Reader stream = new FileReader(path)) {
            JsonReader in = new JsonReader(stream);
            return adapter.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveDotPanel(DotPanel panel) {
        saveDotPanel(PathTools.getRelativeFilePath(PathTools.OutputFileType.GRAPH, panel.getName()), panel);
    }

    public static void saveDotPanel(String filePath, DotPanel panel) {
        File f = new File(filePath);
        try {
            if (!panel.getExporters().isEmpty()) {
                panel.getExporters().get(1).export(panel, f);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveChart(JFreeChart chart) {
        String s = chart.getTitle().getText().replace(" ", "").replace(".", "_");
        saveChart(PathTools.getRelativeFilePath(PathTools.OutputFileType.CHART, s), chart);
    }

    public static void saveChart(String filePath, JFreeChart chart) {
        File f = new File(filePath);
        try {
            ChartUtilities.saveChartAsPNG(f, chart, 1920, 1080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileAppender createLogFileAppender(String filePath) {
        FileAppender fileAppender;
        try {
            fileAppender = new FileAppender(new SimpleLayout(), filePath, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileAppender;
    }

    public static FileWriter createOutputFileWriter(String filePath) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileWriter;
    }

    public static CSVWriter createCSVWriter(String filePath) {
        return new CSVWriter(createOutputFileWriter(filePath));
    }

    public static void saveString(String filePath, String x) {
        try (FileWriter outputFileWriter = createOutputFileWriter(filePath)) {
            outputFileWriter.write(x);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveStrings(String filePath, Iterable<String> strings) {
        try (FileWriter outputFileWriter = createOutputFileWriter(filePath)) {
            for (String string : strings) {
                outputFileWriter.write(string + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveAsCSV(String filePath, List<Tuple2<String, List<String>>> list) {
        try (CSVWriter csvWriter = createCSVWriter(filePath)) {
            csvWriter.writeNext(list.stream().map(Tuple2::getT1).toArray(String[]::new));
            int columns = list.size();
            int rows = list.stream().map(Tuple2::getT2).mapToInt(List::size).min().orElse(0);
            for (int i = 0; i < rows; i++) {
                String[] row = new String[columns];
                for (int j = 0; j < columns; j++) {
                    row[j] = list.get(j).getT2().get(i);
                }
                csvWriter.writeNext(row);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void savePetrinetToPnml(String filePath, ProMPetrinetWrapper pn) {
        if (!filePath.endsWith(".pnml")) filePath = filePath + ".pnml";
        Pnml pnml = new Pnml();
        pnml.setType(Pnml.PnmlType.PNML);
        GraphLayoutConnection layout = new GraphLayoutConnection(pn.getNet());
        Pnml fromNet = pnml.convertFromNet(pn.getNet(), pn.getInitialMarking(), pn.getFinalMarkings(), layout);
        String s = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + fromNet.exportElement(fromNet);
        saveString(filePath, s);
    }
}
