package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.function.Consumer;

public class ActionPipe<O extends Observation> extends IdentityPipe<O> {
    private final Consumer<O> action;

    public ActionPipe(Consumer<O> action) {
        this.action = action;
    }

    @Override
    public void observe(O observation) {
        action.accept(observation);
        super.observe(observation);
    }
}
