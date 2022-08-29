package org.processmining.specpp.supervision.supervisors;

import java.time.Duration;

public class RefreshRates {
    public static final int REFRESH_RATE;
    public static final Duration REFRESH_INTERVAL;
    public static final String REFRESH_STRING;


    static {
        REFRESH_RATE = 10;
        REFRESH_INTERVAL = Duration.ofSeconds(1).dividedBy(REFRESH_RATE);
        REFRESH_STRING = "[\u27F3" + REFRESH_INTERVAL.toMillis() + "ms]";
    }

}
