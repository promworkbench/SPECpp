package org.processmining.specpp.postprocessing;

import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

public class SelfLoopPlaceMerger implements PetriNetPostProcessor {
    @Override
    public PetriNet postProcess(PetriNet input) {
        Set<Place> result = new HashSet<>();

        LinkedList<Place> todo = new LinkedList<>(input.getPlaces());

        while (todo.size() > 1) {
            Place first = todo.removeFirst();
            Place noSelfLoops = first.nonSelfLoops();
            Optional<Place> optional = todo.stream().filter(p -> noSelfLoops.setEquality(p.nonSelfLoops())).findFirst();

            if (optional.isPresent()) {
                Place other = optional.get();
                todo.remove(other);
                todo.addFirst(first.union(other));
            } else result.add(first);
        }
        if (!todo.isEmpty()) result.add(todo.remove());

        return new PetriNet(result);
    }

}
