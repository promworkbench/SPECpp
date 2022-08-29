package org.processmining.specpp.util;


public class PrivatePaths {

    public static final String LOG_PATH = String.join(PathTools.PATH_FOLDER_SEPARATOR, "C:", "Users", "Leah", "Documents", "Event Logs") + PathTools.PATH_FOLDER_SEPARATOR;

    public static final String BPI11 = String.join(PathTools.PATH_FOLDER_SEPARATOR, "BPIC 2011", "Hospital_log.xes");
    public static final String BPI12 = String.join(PathTools.PATH_FOLDER_SEPARATOR, "BPIC 2012", "BPI_Challenge_2012.xes.gz");
    public static final String BPI18 = String.join(PathTools.PATH_FOLDER_SEPARATOR, "BPIC 2018", "BPI Challenge 2018.xes.gz");
    public static final String WILWILLES_REDUCED_NO_PARALELLISM = String.join(PathTools.PATH_FOLDER_SEPARATOR, "Synthetic", "Lisa", "wilwilles-reduced(noParalellism).xes");
    public static final String ROAD_TRAFFIC_FINE_MANAGEMENT_PROCESS = String.join(PathTools.PATH_FOLDER_SEPARATOR, "Synthetic", "Road_Traffic_Fine_Management_Process.xes.gz");

    public static String toPath(String logName) {
        return LOG_PATH + logName;
    }

}
