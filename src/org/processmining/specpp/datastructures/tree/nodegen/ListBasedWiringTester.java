package org.processmining.specpp.datastructures.tree.nodegen;

import org.processmining.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListBasedWiringTester implements WiringTester {

    private final ArrayList<Place> wiredPlaces;

    public ListBasedWiringTester() {
        this.wiredPlaces = new ArrayList<>();
    }

    @Override
    public BitMask filterPotentialSetExpansions(Place place, BitMask expansions, MonotonousPlaceGenerationLogic.ExpansionType expansionType) {
        if (expansions.isEmpty()) return expansions;

        Function<Place, BitEncodedSet<Transition>> getTransitions = expansionType == MonotonousPlaceGenerationLogic.ExpansionType.Postset ? Place::postset : Place::preset;
        Function<Place, BitEncodedSet<Transition>> getOtherTransitions = expansionType == MonotonousPlaceGenerationLogic.ExpansionType.Postset ? Place::preset : Place::postset;

        for (Place wiredPlace : wiredPlaces) {
            if (getOtherTransitions.apply(wiredPlace).getBitMask().intersects(expansions)) {
                expansions.setminus(getTransitions.apply(wiredPlace).getBitMask());
            }
        }

        return expansions;
    }

    private Predicate<Place> overlappingWiringPredicate(Place testPlace) {
        final BitEncodedSet<Transition> preset = testPlace.preset();
        final BitEncodedSet<Transition> postset = testPlace.postset();
        return wiredPlace -> preset.intersects(wiredPlace.preset()) && postset.intersects(wiredPlace.postset());
    }

    private boolean meetsWiringConstraint(Place place) {
        Predicate<Place> isOverlapping = overlappingWiringPredicate(place);
        return wiredPlaces.stream().noneMatch(isOverlapping);
    }

    public void wire(Place place) {
        wiredPlaces.add(place);
    }

    public void unwire(Place place) {
        wiredPlaces.remove(place);
    }

    @Override
    public boolean notAllowedToExpand(PlaceNode placeNode) {
        return !meetsWiringConstraint(placeNode.getPlace());
    }
}
