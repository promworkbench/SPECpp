package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.util.IndexedItem;

public class IndexedVariant extends IndexedItem<Variant> {
    public IndexedVariant(int index, Variant item) {
        super(index, item);
    }

    public Variant getVariant() {
        return getItem();
    }

}
