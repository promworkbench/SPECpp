package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;
import org.processmining.specpp.evaluation.fitness.base.SupportsConsideredVariants;

import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

public abstract class AbstractFitnessEvaluator extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider, SupportsConsideredVariants {

    private final MultiEncodedLog multiEncodedLog;
    private final DataSource<BitMask> variantSubsetSource;
    protected ReplayComputationParameters replayComputationParameters;

    private BitMask consideredVariants;

    public AbstractFitnessEvaluator(MultiEncodedLog multiEncodedLog, DataSource<BitMask> variantSubsetSource, ReplayComputationParameters replayComputationParameters) {
        this.multiEncodedLog = multiEncodedLog;
        this.variantSubsetSource = variantSubsetSource;
        this.replayComputationParameters = replayComputationParameters;
    }

    @Override
    public void updateConsideredVariants() {
        setConsideredVariants(variantSubsetSource.getData());
    }

    @Override
    public BitMask getConsideredVariants() {
        updateConsideredVariants();
        return consideredVariants;
    }

    @Override
    public void setConsideredVariants(BitMask consideredVariants) {
        this.consideredVariants = consideredVariants;
    }


    public MultiEncodedLog getMultiEncodedLog() {
        return multiEncodedLog;
    }

    protected DataSource<BitMask> getVariantSubsetSource() {
        return variantSubsetSource;
    }

    protected Consumer<IndexedItem<Pair<IntBuffer>>> createLambda(BitMask consideredVariants, Place place, ResultUpdater upd, ReplayComputationParameters parameters) {
        IntUnaryOperator presetIndicator = ReplayUtils.presetIndicator(place);
        IntUnaryOperator postsetIndicator = ReplayUtils.postsetIndicator(place);
        IntVector frequencies = getVariantFrequencies();
        boolean clipMarkingAtZero = parameters.isClipMarkingAtZero();
        return ii -> {
            if (consideredVariants == null || consideredVariants.get(ii.getIndex())) {
                Pair<IntBuffer> pair = ii.getItem();
                IntBuffer presetEncodedVariant = pair.first(), postsetEncodedVariant = pair.second();
                int acc = 0;
                boolean wentUnder = false, wentOver = false, activated = false;
                while (presetEncodedVariant.hasRemaining() && postsetEncodedVariant.hasRemaining()) {
                    int i = postsetIndicator.applyAsInt(postsetEncodedVariant.get());
                    acc += i;
                    wentUnder |= acc < 0;
                    activated |= acc != 0;
                    if (clipMarkingAtZero) acc = Math.max(0, acc);
                    int j = presetIndicator.applyAsInt(presetEncodedVariant.get());
                    acc += j;
                    wentOver |= acc > 1;
                    activated |= acc != 0;
                }
                boolean notZeroAtEnd = acc > 0;
                int idx = ii.getIndex();
                int f = frequencies.get(idx);
                upd.update(idx, f, activated, wentUnder, wentOver, notZeroAtEnd);
            }
        };
    }

    protected Spliterator<IndexedItem<Pair<IntBuffer>>> getIndexedItemSpliterator() {
        return getMultiEncodedLog().indexedSpliterator();
    }

    protected Stream<IndexedItem<Pair<IntBuffer>>> getIndexedItemStream() {
        return getMultiEncodedLog().indexedStream(false);
    }

    protected IntVector getVariantFrequencies() {
        return getMultiEncodedLog().getPresetEncodedLog().getVariantFrequencies();
    }

    protected void run(BitMask consideredVariants, Place place, ResultUpdater upd) {
        Spliterator<IndexedItem<Pair<IntBuffer>>> spliterator = getIndexedItemSpliterator();
        spliterator.forEachRemaining(createLambda(consideredVariants, place, upd, replayComputationParameters));
    }

    protected Spliterator<IndexedItem<EnumSet<ReplayOutcome>>> prepareSpliterator(Place place, BitMask consideredVariants) {
        IntUnaryOperator presetIndicator = ReplayUtils.presetIndicator(place);
        IntUnaryOperator postsetIndicator = ReplayUtils.postsetIndicator(place);

        Stream<IndexedItem<Pair<IntBuffer>>> stream = getIndexedItemStream();
        if (consideredVariants != null) stream = stream.filter(ip -> consideredVariants.get(ip.getIndex()));
        return stream.map(ip -> new IndexedItem<>(ip.getIndex(), ReplayUtils.variantReplay(ip.getItem()
                .getT1(), presetIndicator, ip.getItem().getT2(), postsetIndicator))).spliterator();
    }

    @FunctionalInterface
    protected interface ResultUpdater {

        void update(int variantIndex, int variantFrequency, boolean activated, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd);

    }

    public static abstract class Builder extends ComponentSystemAwareBuilder<AbstractFitnessEvaluator> {

        protected final DelegatingDataSource<MultiEncodedLog> multiEncodedLogSource = new DelegatingDataSource<>();
        protected final DelegatingDataSource<BitMask> variantSubsetSource = new DelegatingDataSource<>();

        protected final DelegatingDataSource<ReplayComputationParameters> replayComputationParametersSource = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(DataRequirements.CONSIDERED_VARIANTS, variantSubsetSource)
                    .require(DataRequirements.ENC_LOG, multiEncodedLogSource)
                    .require(ParameterRequirements.REPLAY_COMPUTATION, replayComputationParametersSource);
        }

    }
}
