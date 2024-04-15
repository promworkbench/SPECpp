package org.processmining.specpp.evaluation.fitness.base;

import org.processmining.specpp.datastructures.encoding.BitMask;

public interface SupportsConsideredVariants {
    void updateConsideredVariants();

    BitMask getConsideredVariants();

    void setConsideredVariants(BitMask consideredVariants);

}
