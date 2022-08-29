package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.composition.events.CandidateAcceptanceRevoked;
import org.processmining.specpp.composition.events.CandidateAccepted;
import org.processmining.specpp.composition.events.CandidateCompositionEvent;
import org.processmining.specpp.composition.events.CandidateRejected;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.evaluation.implicitness.*;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.JavaTypingUtils;

public class PlaceComposerWithCIPR<I extends AdvancedComposition<Place>> extends AbstractComposer<Place, I, PetriNet> {
    protected final DelegatingEvaluator<Place, ImplicitnessRating> implicitnessEvaluator = new DelegatingEvaluator<>(p -> BooleanImplicitness.NOT_IMPLICIT);
    private final EventSupervision<CandidateCompositionEvent<Place>> compositionEventSupervision = PipeWorks.eventSupervision();

    public PlaceComposerWithCIPR(I composition) {
        super(composition, c -> new PetriNet(c.toSet()));
        globalComponentSystem()
                .provide(SupervisionRequirements.observable("composer.events", JavaTypingUtils.castClass(CandidateCompositionEvent.class), compositionEventSupervision));
        localComponentSystem()
                .require(EvaluationRequirements.PLACE_IMPLICITNESS, implicitnessEvaluator);
    }

    @Override
    protected boolean deliberateAcceptance(Place candidate) {
        ImplicitnessRating rating = implicitnessEvaluator.eval(candidate);
        if (rating instanceof ReplaceExaminedPlace) {
            return false;
        } else if (rating instanceof ReplaceExistingPlace) {
            Place p1 = ((ReplaceExistingPlace) rating).getCandidate();
            Place p2 = ((ReplaceExistingPlace) rating).getExisting();
            Place p3 = ((ReplaceExistingPlace) rating).getReplacement();
            revokeAcceptance(p2);
            //acceptCandidate(p3);
            return true;
        } else if (rating instanceof ReplacementPlaceInfeasible) return true;
        else if (rating instanceof BooleanImplicitness) return rating == BooleanImplicitness.NOT_IMPLICIT;
        else return false;
    }

    @Override
    protected void candidateAccepted(Place candidate) {
        compositionEventSupervision.observe(new CandidateAccepted<>(candidate));
    }

    @Override
    protected void candidateRejected(Place candidate) {
        compositionEventSupervision.observe(new CandidateRejected<>(candidate));
    }

    @Override
    protected void acceptanceRevoked(Place candidate) {
        compositionEventSupervision.observe(new CandidateAcceptanceRevoked<>(candidate));
    }


    @Override
    protected void initSelf() {

    }

    @Override
    public void candidatesAreExhausted() {

    }
}
