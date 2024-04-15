package org.processmining.specpp.composition.composers.eventing;

import org.processmining.specpp.base.Result;
import org.processmining.specpp.base.impls.CandidateConstraint;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.composition.composers.AbsoluteFitnessFilter;
import org.processmining.specpp.composition.events.CandidateCompositionEvent;
import org.processmining.specpp.composition.events.CandidateRejected;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.JavaTypingUtils;

// solve this via configurable builders
public class EventingAbsoluteFitnessFilter<I extends CompositionComponent<Place>, R extends Result> extends AbsoluteFitnessFilter<I, R> {

    private final EventSupervision<CandidateCompositionEvent<Place>> composerEventSupervision = PipeWorks.eventSupervision();


    public EventingAbsoluteFitnessFilter(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
        globalComponentSystem().provide(SupervisionRequirements.observable("composer.filter.events", JavaTypingUtils.castClass(CandidateCompositionEvent.class), composerEventSupervision));
    }


    @Override
    protected void gotFiltered(Place place, CandidateConstraint<Place> constraint) {
        super.gotFiltered(place, constraint);
        composerEventSupervision.observe(new CandidateRejected<>(place));
    }

    @Override
    protected void initSelf() {

    }
}
