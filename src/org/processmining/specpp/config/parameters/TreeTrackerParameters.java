package org.processmining.specpp.config.parameters;

public class TreeTrackerParameters implements Parameters {


    private final long eventLimit;
    private final int fromLevel;
    private final int toLevel;
    private final long printNodeLimit;
    private final int drawNodeLimit;

    public static TreeTrackerParameters getDefault() {
        return new TreeTrackerParameters(0, 3, 1_000_000_000L, 100, 100);
    }

    public TreeTrackerParameters(int fromLevel, int toLevel, long eventLimit, long printNodeLimit, int drawNodeLimit) {
        this.fromLevel = fromLevel;
        this.toLevel = toLevel;
        this.printNodeLimit = printNodeLimit;
        this.eventLimit = eventLimit;
        this.drawNodeLimit = drawNodeLimit;
    }

    public long getEventLimit() {
        return eventLimit;
    }

    public int getFromLevel() {
        return fromLevel;
    }

    public int getToLevel() {
        return toLevel;
    }

    public long getPrintNodeLimit() {
        return printNodeLimit;
    }

    public int getDrawNodeLimit() {
        return drawNodeLimit;
    }


    @Override
    public String toString() {
        return "TreeTrackerParameters{" + "eventLimit=" + eventLimit + ", fromLevel=" + fromLevel + ", toLevel=" + toLevel + ", printNodeLimit=" + printNodeLimit + ", drawNodeLimit=" + drawNodeLimit + '}';
    }
}
