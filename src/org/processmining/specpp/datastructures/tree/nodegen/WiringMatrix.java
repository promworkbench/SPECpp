package org.processmining.specpp.datastructures.tree.nodegen;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;

import java.util.PrimitiveIterator;

public class WiringMatrix implements WiringTester {
    private final BitMask[] rowSets;
    private final BitMask[] colSets;

    public WiringMatrix(IntEncodings<Transition> transitionEncodings) {
        rowSets = new BitMask[transitionEncodings.pre().size()];
        for (int i = 0; i < rowSets.length; i++) {
            rowSets[i] = new BitMask();
        }
        colSets = new BitMask[transitionEncodings.post().size()];
        for (int i = 0; i < colSets.length; i++) {
            colSets[i] = new BitMask();
        }
    }


    @Override
    public void wire(Place place) {
        BitMask preset = place.preset().getBitMask();
        BitMask postset = place.postset().getBitMask();
        updateSets(rowSets, preset, postset);
        updateSets(colSets, postset, preset);
    }

    private void updateSets(BitMask[] sets, BitMask idxs, BitMask vals) {
        PrimitiveIterator.OfInt rowIdxIterator = idxs.iterator();
        while (rowIdxIterator.hasNext()) {
            int next = rowIdxIterator.nextInt();
            sets[next].union(vals);
        }
    }

    @Override
    public BitMask filterPotentialSetExpansions(Place place, BitMask expansions, MonotonousPlaceGenerationLogic.ExpansionType expansionType) {
        if (expansionType == MonotonousPlaceGenerationLogic.ExpansionType.Preset) {
            PrimitiveIterator.OfInt colIdxIterator = place.postset().getBitMask().iterator();
            while (colIdxIterator.hasNext()) {
                int next = colIdxIterator.nextInt();
                expansions.setminus(colSets[next]);
            }
        } else {
            PrimitiveIterator.OfInt rowIdxIterator = place.preset().getBitMask().iterator();
            while (rowIdxIterator.hasNext()) {
                int next = rowIdxIterator.nextInt();
                expansions.setminus(rowSets[next]);
            }
        }
        return expansions;
    }


    @Override
    public boolean notAllowedToExpand(PlaceNode placeNode) {
        Place place = placeNode.getPlace();
        BitMask preset = place.preset().getBitMask();
        BitMask postset = place.postset().getBitMask();
        PrimitiveIterator.OfInt rowIdxIterator = preset.iterator();
        while (rowIdxIterator.hasNext()) {
            int next = rowIdxIterator.nextInt();
            if (postset.intersects(rowSets[next])) return true;
        }
        return false;
    }

    @Override
    public void unwire(Place place) {
        throw new UnsupportedOperationException();
    }
}
