package org.processmining.specpp.composition.composers;

import org.processmining.specpp.base.ConstrainingComposer;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.base.impls.CandidateConstraint;
import org.processmining.specpp.base.impls.FilteringComposer;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.util.caching.BasicCache;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.JavaTypingUtils;

public abstract class ConstrainingFilteringPlaceComposer<I extends CompositionComponent<Place>, R extends Result> extends FilteringComposer<Place, I, R> implements ConstrainingComposer<Place, I, R, CandidateConstraint<Place>> {
    protected final EventSupervision<CandidateConstraint<Place>> constraintEvents = PipeWorks.eventSupervision();

    public ConstrainingFilteringPlaceComposer(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
        globalComponentSystem().provide(SupervisionRequirements.observable("composer.constraints.under_over_fed", getPublishedConstraintClass(), getConstraintPublisher()));
        localComponentSystem().provide(SupervisionRequirements.observable("composer.constraints.under_over_fed", getPublishedConstraintClass(), getConstraintPublisher()));
    }

    @Override
    protected void initSelf() {

    }

    @Override
    public Observable<CandidateConstraint<Place>> getConstraintPublisher() {
        return constraintEvents;
    }

    @Override
    public Class<CandidateConstraint<Place>> getPublishedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
    }

    protected void gotFiltered(Place place, CandidateConstraint<Place> constraint) {
        if (constraint != null) constraintEvents.observe(constraint);
    }

}
