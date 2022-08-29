package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.datastructures.encoding.IndexSubset;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.OnlyCoversIndexSubset;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.datastructures.vectorization.IntVectorSubsetStorage;

public class EncodedSubLogImpl extends EncodedLogImpl implements OnlyCoversIndexSubset {
    private final IndexSubset indexSubset;

    public EncodedSubLogImpl(IntVector variantFrequencies, IntVectorSubsetStorage ivss, IntEncoding<Activity> encoding) {
        super(variantFrequencies, ivss, encoding);
        indexSubset = ivss.getIndexSubset();
    }

    @Override
    public IndexSubset getIndexSubset() {
        return indexSubset;
    }
}
