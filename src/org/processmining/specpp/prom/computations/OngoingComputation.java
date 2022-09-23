package org.processmining.specpp.prom.computations;

import com.google.common.util.concurrent.ListenableFuture;
import org.processmining.specpp.supervision.piping.AbstractAsyncAwareObservable;

import java.time.Duration;
import java.time.LocalDateTime;

public class OngoingComputation extends AbstractAsyncAwareObservable<ComputationEvent> {

    private LocalDateTime start, end;
    private Duration timeLimit;
    private boolean gracefullyCancelled, forciblyCancelled;
    private ListenableFuture<?> computationFuture;
    private Runnable cancellationCallback;

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        assert this.start == null;
        this.start = start;
        publish(new ComputationStarted(start));
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        assert this.end == null;
        this.end = end;
        publish(new ComputationEnded(end));
    }

    public Duration getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Duration timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Duration calculateRemainingTime() {
        if (timeLimit == null || start == null) return null;
        else return timeLimit.minus(Duration.between(start, LocalDateTime.now()));
    }

    public void markGracefullyCancelled() {
        gracefullyCancelled = true;
        publish(new ComputationCancelled(true));
    }

    public void markForciblyCancelled() {
        forciblyCancelled = true;
        publish(new ComputationCancelled(false));
    }

    public boolean isCancelled() {
        return gracefullyCancelled || forciblyCancelled;
    }

    public ListenableFuture<?> getComputationFuture() {
        return computationFuture;
    }

    public void setComputationFuture(ListenableFuture<?> computationFuture) {
        this.computationFuture = computationFuture;
    }

    public Runnable getCancellationCallback() {
        return cancellationCallback;
    }

    public void setCancellationCallback(Runnable cancellationCallback) {
        this.cancellationCallback = cancellationCallback;
    }

    public boolean hasEnded() {
        return end != null;
    }

    public boolean isRunning() {
        return start != null && computationFuture != null && end != null;
    }

    public Duration calculateRuntime() {
        return Duration.between(start, end);
    }

    public boolean hasTerminatedSuccessfully() {
        return hasEnded() && !forciblyCancelled;
    }
}
