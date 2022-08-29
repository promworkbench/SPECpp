package org.processmining.specpp.datastructures.tree.nodegen;

import org.processmining.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;

public class TransitionBlacklister implements PotentialExpansionsFilter {

    private final BitEncodedSet<Transition> presetBlacklist, postsetBlacklist;

    public TransitionBlacklister(IntEncodings<Transition> transitionEncodings) {
        presetBlacklist = BitEncodedSet.empty(transitionEncodings.pre());
        postsetBlacklist = BitEncodedSet.empty(transitionEncodings.post());
    }

    public void blacklist(Transition transition) {
        presetBlacklist.add(transition);
        postsetBlacklist.add(transition);
    }

    @Override
    public BitMask filterPotentialSetExpansions(Place place, BitMask expansions, MonotonousPlaceGenerationLogic.ExpansionType expansionType) {
        if (expansions.isEmpty()) return expansions;

        if (expansionType == MonotonousPlaceGenerationLogic.ExpansionType.Postset)
            expansions.setminus(postsetBlacklist.getBitMask());
        else expansions.setminus(presetBlacklist.getBitMask());

        return expansions;
    }

}
