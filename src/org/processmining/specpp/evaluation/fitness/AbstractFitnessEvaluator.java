package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;

public abstract class AbstractFitnessEvaluator extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider {

    private final MultiEncodedLog multiEncodedLog;
    private final DataSource<BitMask> variantSubsetSource;

    private BitMask consideredVariants;

    public AbstractFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource) {
        this.multiEncodedLog = multiEncodedLog;
        this.variantSubsetSource = variantSubsetSource;
    }

    public void updateConsideredVariants() {
        setConsideredVariants(variantSubsetSource.getData());
    }

    public BitMask getConsideredVariants() {
        updateConsideredVariants();
        return consideredVariants;
    }

    public void setConsideredVariants(BitMask consideredVariants) {
        this.consideredVariants = consideredVariants;
    }


    public MultiEncodedLog getMultiEncodedLog() {
        return multiEncodedLog;
    }

    protected DataSource<BitMask> getVariantSubsetSource() {
        return variantSubsetSource;
    }

}
