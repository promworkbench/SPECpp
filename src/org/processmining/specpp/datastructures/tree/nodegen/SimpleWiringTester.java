package org.processmining.specpp.datastructures.tree.nodegen;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;

import java.util.PrimitiveIterator;

public class SimpleWiringTester implements WiringTester {

    private final IntEncodings<Transition> transitionEncodings;
    private final boolean[][] booleans;
    private final int n;
    private final int m;

    public SimpleWiringTester(IntEncodings<Transition> transitionEncodings) {
        this.transitionEncodings = transitionEncodings;
        n = transitionEncodings.pre().size();
        m = transitionEncodings.post().size();
        booleans = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            booleans[i] = new boolean[m];
        }
    }

    @Override
    public BitMask filterPotentialSetExpansions(Place place, BitMask expansions, MonotonousPlaceGenerationLogic.ExpansionType expansionType) {
        if (expansionType == MonotonousPlaceGenerationLogic.ExpansionType.Postset) {
            PrimitiveIterator.OfInt it = place.preset().streamIndices().iterator();
            while (it.hasNext()) {
                int i = it.nextInt();
                for (int j = 0; j < m; j++) {
                    if (booleans[i][j]) expansions.clear(j);
                }
            }
        } else {
            PrimitiveIterator.OfInt it = place.postset().streamIndices().iterator();
            while (it.hasNext()) {
                int j = it.nextInt();
                for (int i = 0; i < n; i++) {
                    if (booleans[i][j]) expansions.clear(i);
                }
            }
        }
        return expansions;
    }

    @Override
    public void wire(Place place) {
        place.preset()
             .streamIndices()
             .forEach(i -> place.postset().streamIndices().forEach(j -> booleans[i][j] = true));
    }

    @Override
    public void unwire(Place place) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWired(Place place) {
        return place.preset()
                    .streamIndices()
                    .anyMatch(i -> place.postset().streamIndices().anyMatch(j -> booleans[i][j]));
    }
}
