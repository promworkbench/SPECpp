package org.processmining.specpp.composition.composers;

import org.apache.commons.collections4.BidiMap;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.AbstractComposer;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.datastructures.vectorization.OrderingRelation;
import org.processmining.specpp.datastructures.vectorization.VMHComputations;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;
import org.processmining.specpp.evaluation.implicitness.BooleanImplicitness;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.observations.DebugEvent;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.JavaTypingUtils;

import java.nio.IntBuffer;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Spliterator;

public class ComposerDelevopmentEntryPoint<I extends AdvancedComposition<Place>> extends AbstractComposer<Place, I, CollectionOfPlaces> {

    private final DelegatingDataSource<Log> logSource = new DelegatingDataSource<>();
    private final DelegatingDataSource<BidiMap<Activity, Transition>> actTransMapping = new DelegatingDataSource<>();

    private final DelegatingEvaluator<Place, ImplicitnessRating> implicitnessEvaluator = new DelegatingEvaluator<>();
    private final DelegatingEvaluator<Place, VariantMarkingHistories> markingHistoriesEvaluator = new DelegatingEvaluator<>();
    private final DelegatingEvaluator<EvaluationParameterTuple2<Place, Collection<Place>>, BooleanImplicitness> directImplicitnessEvaluator = new DelegatingEvaluator<>();
    private final EventSupervision<DebugEvent> eventSupervisor = PipeWorks.eventSupervision();

    public ComposerDelevopmentEntryPoint(I composition) {
        super(composition, c -> new CollectionOfPlaces(c.toList()));
        globalComponentSystem().require(DataRequirements.RAW_LOG, logSource)
                               .require(DataRequirements.ACT_TRANS_MAPPING, actTransMapping)
                               .require(EvaluationRequirements.PLACE_MARKING_HISTORY, markingHistoriesEvaluator)
                               .require(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), BooleanImplicitness.class), directImplicitnessEvaluator)
                               .provide(SupervisionRequirements.observable("felix.debug", DebugEvent.class, eventSupervisor));
        localComponentSystem().require(EvaluationRequirements.PLACE_IMPLICITNESS, implicitnessEvaluator);
    }

    @Override
    protected boolean deliberateAcceptance(Place candidate) {
        // here's some more sample code

        VariantMarkingHistories variantMarkingHistories = markingHistoriesEvaluator.eval(candidate);

        // iterating over all variant vectors
        // spliterators are used to support fork-join like computation, see ForkJoinFitnessEvaluator
        // and streaming (obviously adds overhead)
        Spliterator<IntBuffer> vectorSpliterator = variantMarkingHistories.getData().spliterator();
        vectorSpliterator.forEachRemaining(bf -> {

            // iterating over one "marking history", i.e., abbce -> 01101
            while (bf.hasRemaining()) {
                int i = bf.get();

            }
        });

        // if you need the variant index
        variantMarkingHistories.indexedSpliterator().forEachRemaining(ii -> {
            IntBuffer bf = ii.getItem();

        });
        //

        // VariantMarkingHistories can be added, subtracted, negated
        // they are also comparable
        VariantMarkingHistories h1 = null, h2 = null;
        boolean gt = h1.gt(h2); // greater than, i.e., [00120-1] > [00110-1]. see replay based impl. place removal
        // VMH Computations provides some other methods
        EnumSet<OrderingRelation> orderingRelations = VMHComputations.orderingRelations(h1, h2);
        // methods often have an "[...]On" variant. It can be used to restrict the computation to bit sets of indices to consider
        EnumSet<OrderingRelation> orderingRelationsOn = VMHComputations.orderingRelationsOn(h1.getIndexSubset()
                                                                                              .getIndices(), h1, h2);
        // index subsets are used to support sub log computations, e.g., computing something separately on variants 0-x and x-end and composing the results


        ImplicitnessRating implicitnessRating = implicitnessEvaluator.eval(candidate);
        // or directly
        BooleanImplicitness otherImplicitnessRating = directImplicitnessEvaluator.eval(new EvaluationParameterTuple2<>(candidate, composition.toList()));// toList/toSet are inefficient copying operations

        if (implicitnessRating == BooleanImplicitness.IMPLICIT) eventSupervisor.observe(new DebugEvent("read me"));
        else eventSupervisor.observe(new DebugEvent("alternatively, read me"));

        // previously accepted places
        for (Place place : composition) {

        }
        Place other = null;
        revokeAcceptance(other);

        return false;
    }

    @Override
    protected void acceptanceRevoked(Place candidate) {
        // update some state
    }

    @Override
    protected void candidateAccepted(Place candidate) {
        // update some state
    }

    @Override
    protected void candidateRejected(Place candidate) {

    }

    @Override
    public void candidatesAreExhausted() {

    }

    @Override
    public boolean isFinished() {
        // stop place proposal/candidate search prematurely
        return false;
    }

    @Override
    protected void initSelf() {
        Log log = logSource.getData();
        // calc. prefix tree etc.
        for (IndexedVariant indexedVariant : log) {
            Variant variant = indexedVariant.getVariant();
            int frequency = log.getVariantFrequency(indexedVariant.getIndex());
            for (Activity activity : variant) {

            }
        }

    }
}
