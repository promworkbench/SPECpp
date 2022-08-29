package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.traits.Triggerable;

public interface Buffering extends Triggerable {

    void flushBuffer();

    boolean isBufferNonEmpty();

    @Override
    default void trigger() {
        flushBuffer();
    }

}
