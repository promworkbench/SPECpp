package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.specpp.supervision.piping.TimeStopper;

public abstract class AbstractFitnessEvaluator extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider {

    private final DelegatingDataSource<MultiEncodedLog> multiEncodedLogSource = new DelegatingDataSource<>();
    private final DelegatingDataSource<BitMask> variantSubsetSource = new DelegatingDataSource<>();

    protected final TimeStopper timeStopper = new TimeStopper();
    private BitMask consideredVariants;

    public AbstractFitnessEvaluator(DataSource<MultiEncodedLog> multiEncodedLogDataSource, DataSource<BitMask> variantSubsetSource) {
        this.multiEncodedLogSource.setDelegate(multiEncodedLogDataSource);
        this.variantSubsetSource.setDelegate(variantSubsetSource);
    }

    public AbstractFitnessEvaluator() {
        globalComponentSystem().require(DataRequirements.CONSIDERED_VARIANTS, variantSubsetSource)
                               .require(DataRequirements.ENC_LOG, multiEncodedLogSource)
                               .provide(SupervisionRequirements.observable("evaluator.performance", PerformanceEvent.class, timeStopper));
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
        return multiEncodedLogSource.getData();
    }

}
