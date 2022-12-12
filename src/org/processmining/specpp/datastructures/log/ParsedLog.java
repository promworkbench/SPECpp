package org.processmining.specpp.datastructures.log;

import org.apache.commons.collections4.BidiMap;

import java.util.Objects;

public class ParsedLog {

    private final Log log;
    private final BidiMap<String, Activity> stringActivityMapping;

    public Log getLog() {
        return log;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParsedLog parsedLog = (ParsedLog) o;

        if (!Objects.equals(log, parsedLog.log)) return false;
        return Objects.equals(stringActivityMapping, parsedLog.stringActivityMapping);
    }

    @Override
    public int hashCode() {
        int result = log != null ? log.hashCode() : 0;
        result = 31 * result + (stringActivityMapping != null ? stringActivityMapping.hashCode() : 0);
        return result;
    }

    public BidiMap<String, Activity> getStringActivityMapping() {
        return stringActivityMapping;
    }

    public ParsedLog(Log log, BidiMap<String, Activity> stringActivityMapping) {
        this.log = log;
        this.stringActivityMapping = stringActivityMapping;
    }
}
