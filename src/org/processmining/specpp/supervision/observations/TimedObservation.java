package org.processmining.specpp.supervision.observations;


import java.time.LocalDateTime;
import java.util.Objects;

public class TimedObservation<O extends Observation> implements Observation {

    private final LocalDateTime localDateTime;
    private final O observation;


    public TimedObservation(LocalDateTime localDateTime, O observation) {
        this.localDateTime = localDateTime;
        this.observation = observation;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public O getObservation() {
        return observation;
    }

    @Override
    public String toString() {
        return "TimedObservation@" + localDateTime.toString() + "{" + observation + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimedObservation<?> that = (TimedObservation<?>) o;

        if (!Objects.equals(localDateTime, that.localDateTime)) return false;
        return Objects.equals(observation, that.observation);
    }

    @Override
    public int hashCode() {
        int result = localDateTime != null ? localDateTime.hashCode() : 0;
        result = 31 * result + (observation != null ? observation.hashCode() : 0);
        return result;
    }
}
