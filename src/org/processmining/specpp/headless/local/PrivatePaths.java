package org.processmining.specpp.headless.local;


import org.processmining.specpp.util.PathTools;

public class PrivatePaths {

    public static final String LOG_PATH = PathTools.join("C:", "Users", "Leah", "Documents", "Event Logs") + PathTools.PATH_FOLDER_SEPARATOR;

    public static final String BPI11 = PathTools.join("BPIC 2011", "Hospital_log.xes");
    public static final String BPI12 = PathTools.join("BPIC 2012", "BPI_Challenge_2012.xes.gz");
    public static final String BPI18 = PathTools.join("BPIC 2018", "BPI Challenge 2018.xes.gz");
    public static final String WILWILLES_REDUCED_NO_PARALELLISM = PathTools.join("Synthetic", "Lisa", "wilwilles-reduced(noParalellism).xes");
    public static final String ROAD_TRAFFIC_FINE_MANAGEMENT_PROCESS = PathTools.join("Synthetic", "Road_Traffic_Fine_Management_Process.xes");
    public static final String BPIC12_A_projection = PathTools.join("BPIC 2012", "A_projection.xes");
    public static final String BPIC12_O_projection = PathTools.join("BPIC 2012", "O_projection.xes");
    public static final String BPIC12_W_projection = PathTools.join("BPIC 2012", "W_projection.xes");
    public static final String Teleclaims = PathTools.join("Felix", "Teleclaims.xes");

    public static String toAbsolutePath(String logName) {
        return LOG_PATH + logName;
    }

}
