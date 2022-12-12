package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.config.components.SimpleBuilder;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;

import java.util.Arrays;

public interface LogBuilder<L extends Log> extends SimpleBuilder<L> {

    LogBuilder<L> appendVariant(Variant variant, int frequency);

    default LogBuilder<L> appendVariant(Variant variant) {
        return appendVariant(variant, 1);
    }

    void setVariants(Variant[] variants);

    void setFrequencies(int[] frequencies);

    default void setSingleVariants(Variant... variants) {
        int[] f = new int[variants.length];
        setVariants(variants);
        Arrays.fill(f, 1);
        setFrequencies(f);
    }

}
