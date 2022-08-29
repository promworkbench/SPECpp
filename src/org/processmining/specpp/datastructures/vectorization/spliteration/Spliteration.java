package org.processmining.specpp.datastructures.vectorization.spliteration;

import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.util.Tuple2;

import java.util.Spliterator;

public class Spliteration {


    public static <T> CompatiblePairSpliteratorImpl<IndexedItem<T>, IndexedItem<Tuple2<T, T>>> joinIndexedSpliterators(Spliterator<IndexedItem<T>> spliteratorA, Spliterator<IndexedItem<T>> spliteratorB) {
        return new CompatiblePairSpliteratorImpl<>(spliteratorA, spliteratorB, tup -> {
            IndexedItem<T> t1 = tup.getT1();
            IndexedItem<T> t2 = tup.getT2();
            return new IndexedItem<>(t1.getIndex(), new ImmutablePair<>(t1.getItem(), t2.getItem()));
        });
    }
}
