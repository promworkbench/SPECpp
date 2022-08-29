package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

public class SelfLoopPlaceMerger implements PostProcessor<PetriNet, PetriNet> {
    @Override
    public PetriNet postProcess(PetriNet input) {
        Set<Place> places = input.getPlaces();
        Set<Place> result = new HashSet<>();

        LinkedList<Place> list = new LinkedList<>(places);

        while (list.size() > 1) {
            Place first = list.removeFirst();
            Place noSelfLoops = first.nonSelfLoops();
            Optional<Place> optional = list.stream().filter(p -> noSelfLoops.setEquality(p.nonSelfLoops())).findFirst();

            if (optional.isPresent()) {
                Place other = optional.get();
                list.remove(other);
                list.addFirst(first.union(other));
            } else result.add(first);
        }
        if (!list.isEmpty()) result.add(list.remove());

        return new PetriNet(result);
    }


}
