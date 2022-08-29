package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IndexSubset;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.OnlyCoversIndexSubset;

import java.util.stream.IntStream;

public class MultiEncodedSubLog extends MultiEncodedLog implements OnlyCoversIndexSubset {

    protected final IndexSubset indexSubset;

    protected MultiEncodedSubLog(IndexSubset indexSubset, EncodedSubLogImpl presetEncodedLog, EncodedSubLogImpl postsetEncodedLog, IntEncodings<Activity> activityEncodings) {
        super(presetEncodedLog, postsetEncodedLog, activityEncodings);
        assert presetEncodedLog.getIndexSubset().setEquality(postsetEncodedLog.getIndexSubset());
        this.indexSubset = indexSubset;
    }

    public int getVariantCount() {
        return indexSubset.getIndexCount();
    }

    public BitMask variantIndices() {
        return indexSubset.getIndices();
    }

    @Override
    public IntStream streamIndices() {
        return indexSubset.streamIndices();
    }

    public IndexSubset getIndexSubset() {
        return indexSubset;
    }

}
