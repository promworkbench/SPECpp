package org.processmining.specpp.datastructures.tree.nodegen;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.util.NaivePlaceMaker;

import java.util.Arrays;
import java.util.PrimitiveIterator;

public class WiringMatrix implements WiringTester {
    private final BitMask[] rowSets;
    private final IntEncodings<Transition> transitionEncodings;
    private final BitMask[] colSets;
    private Place test;

    public WiringMatrix(IntEncodings<Transition> transitionEncodings) {
        rowSets = new BitMask[transitionEncodings.pre().size()];
        this.transitionEncodings = transitionEncodings;
        for (int i = 0; i < rowSets.length; i++) {
            rowSets[i] = new BitMask();
        }
        colSets = new BitMask[transitionEncodings.post().size()];
        for (int i = 0; i < colSets.length; i++) {
            colSets[i] = new BitMask();
        }
        NaivePlaceMaker pm = new NaivePlaceMaker(transitionEncodings);
        //test = pm.preset("incoming claim").postset("S register claim").get();
        //test = pm.preset("initiate payment").postset("close claim").get();
        test = pm.preset("incoming claim", "determine likelihood of claim")
                 .postset("B register claim", "S register claim", "end")
                 .get();
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

    private void filterSets(BitMask[] sets, BitMask idxs, BitMask vals) {
        PrimitiveIterator.OfInt rowIdxIterator = idxs.iterator();
        while (rowIdxIterator.hasNext()) {
            int next = rowIdxIterator.nextInt();
            vals.setminus(sets[next]);
        }
    }

    @Override
    public BitMask filterPotentialSetExpansions(Place place, BitMask expansions, MonotonousPlaceGenerationLogic.ExpansionType expansionType) {
        BitMask[] relevant = expansionType == MonotonousPlaceGenerationLogic.ExpansionType.Preset ? colSets : rowSets;
        BitMask idxs = expansionType == MonotonousPlaceGenerationLogic.ExpansionType.Preset ? place.postset()
                                                                                                   .getBitMask() : place.preset()
                                                                                                                        .getBitMask();
        filterSets(relevant, idxs, expansions);
        return expansions;
    }


    @Override
    public boolean isWired(Place place) {
        BitMask preset = place.preset().getBitMask();
        BitMask postset = place.postset().getBitMask();
        PrimitiveIterator.OfInt rowIdxIterator = preset.iterator();
        while (rowIdxIterator.hasNext()) {
            int next = rowIdxIterator.nextInt();
            if (postset.intersects(rowSets[next])) return true;
        }
        return false;
    }

    protected void reset() {
        for (BitMask bm : rowSets) {
            bm.clear();
        }
        for (BitMask bm : colSets) {
            bm.clear();
        }
    }

    @Override
    public String toString() {
        return "WiringMatrix{rowSets=" + Arrays.toString(rowSets) + ", colSets=" + Arrays.toString(colSets) + "}";
    }

    @Override
    public void unwire(Place place) {
        throw new UnsupportedOperationException();
    }
}
