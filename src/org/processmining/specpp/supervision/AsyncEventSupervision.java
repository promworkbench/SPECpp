package org.processmining.specpp.supervision;

import org.processmining.specpp.supervision.observations.Event;
import org.processmining.specpp.supervision.piping.AsyncIdentityPipe;
import org.processmining.specpp.supervision.traits.OneToOne;

import java.util.concurrent.CompletableFuture;

public class AsyncEventSupervision<E extends Event> extends AsyncIdentityPipe<E> implements OneToOne<E, E> {

    @Override
    public void observe(E observation) {
        observeAsync(CompletableFuture.completedFuture(observation));
    }

}
