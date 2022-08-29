package org.processmining.specpp.util;

import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileUtils {

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

    public static void saveString(String filePath, String x) {
        try (FileWriter outputFileWriter = createOutputFileWriter(filePath)) {
            outputFileWriter.write(x);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveStrings(String filePath, List<String> strings) {

        try (FileWriter outputFileWriter = createOutputFileWriter(filePath)) {
            for (String string : strings) {
                outputFileWriter.write(string + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
