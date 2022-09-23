package org.processmining.specpp.composition;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.Constrainer;
import org.processmining.specpp.base.impls.CandidateConstraint;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.constraints.AddWiredPlace;
import org.processmining.specpp.datastructures.tree.constraints.RemoveWiredPlace;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.JavaTypingUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ConstrainingPlaceCollection extends AbstractBaseClass implements AdvancedComposition<Place>, Constrainer<CandidateConstraint<Place>> {

    private final EventSupervision<CandidateConstraint<Place>> constraintOutput = PipeWorks.eventSupervision();
    private final AdvancedComposition<Place> composition;

    public ConstrainingPlaceCollection(AdvancedComposition<Place> composition) {
        this.composition = composition;
        globalComponentSystem().provide(SupervisionRequirements.observable("composition.constraints.wiring", getPublishedConstraintClass(), getConstraintPublisher()));
        localComponentSystem().provide(SupervisionRequirements.observable("composition.constraints.wiring", getPublishedConstraintClass(), getConstraintPublisher()));
        registerSubComponent(composition);
    }

    @Override
    public void accept(Place place) {
        composition.accept(place);
        constraintOutput.observe(new AddWiredPlace(place));
    }

    @Override
    public void remove(Place candidate) {
        composition.remove(candidate);
        constraintOutput.observe(new RemoveWiredPlace(candidate));
    }

    @Override
    public Place removeLast() {
        return composition.removeLast();
    }

    @Override
    public Observable<CandidateConstraint<Place>> getConstraintPublisher() {
        return constraintOutput;
    }

    @Override
    public Class<CandidateConstraint<Place>> getPublishedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
    }

    @Override
    public Iterator<Place> iterator() {
        return composition.iterator();
    }

    @Override
    protected void initSelf() {

    }

    @Override
    public int size() {
        return composition.size();
    }

    @Override
    public int maxSize() {
        return composition.maxSize();
    }

    @Override
    public boolean hasCapacityLeft() {
        return composition.hasCapacityLeft();
    }

    @Override
    public Set<Place> toSet() {
        return composition.toSet();
    }

    @Override
    public List<Place> toList() {
        return composition.toList();
    }
}
