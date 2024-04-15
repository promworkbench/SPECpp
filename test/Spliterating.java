import org.junit.Assert;
import org.junit.Test;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IndexSubset;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.datastructures.vectorization.IntVectorSubsetStorage;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

public class Spliterating {

    @Test
    public void indexsubsets() {
        BitMask m = new BitMask();
        m.set(2, 4);
        m.set(8);
        m.set(10);
        m.set(13);
        Integer[] indexArr = m.stream().boxed().toArray(Integer[]::new);
        IndexSubset subset = IndexSubset.of(m);

        IntVectorSubsetStorage ivss = IntVectorSubsetStorage.zeros(subset, m.stream().toArray());

        System.out.println(subset);
        System.out.println(ivss);
        for (int i = m.nextSetBit(0); i < m.length() && i >= 0; i = m.nextSetBit(i + 1)) {
            System.out.println(i + ": " + ivss.getVectorSpliterator(i).estimateSize());
        }
        System.out.println("=================");

        assertSpliterator(ivss.spliterator(), indexArr, false);
        assertIndexedSpliterator(ivss.indexedSpliterator(), indexArr, false);

        BitMask tm = new BitMask();
        tm.set(3);
        tm.set(10);
        tm.set(13);
        Integer[] tmArr = tm.stream().boxed().toArray(Integer[]::new);
        assertSpliterator(ivss.spliterator(tm), tmArr, false);
        assertIndexedSpliterator(ivss.indexedSpliterator(tm), tmArr, false);

        IndexSubset ts = IndexSubset.of(tm);
        VariantMarkingHistories sh = new VariantMarkingHistories(ts, ivss);

        assertIndexedSpliterator(sh.indexedSpliterator(), tmArr, false);
        assertIndexedSpliterator(sh.indexedSpliterator(tm), tmArr, true);
    }

    public void assertIndexedSpliterator(Spliterator<IndexedItem<IntBuffer>> spliterator, Integer[] arr, boolean print) {
        Assert.assertEquals(spliterator.estimateSize(), arr.length);
        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();
        spliterator.forEachRemaining(ii -> {
            l1.add((int) ii.getItem().remaining());
            l2.add(ii.getIndex());
        });
        if (print) for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i] + "\t" + l1.get(i) + "\t" + l2.get(i));
        }
        Assert.assertArrayEquals(l1.toArray(), arr);
        Assert.assertArrayEquals(l2.toArray(), arr);
        Assert.assertArrayEquals(l1.toArray(), l2.toArray());
    }

    public void assertSpliterator(Spliterator<IntBuffer> spliterator, Integer[] arr, boolean print) {
        Assert.assertEquals(spliterator.estimateSize(), arr.length);
        List<Integer> l = new ArrayList<>();
        spliterator.forEachRemaining(v -> l.add((int) v.remaining()));
        if (print) for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i] + "\t" + l.get(i));
        }
        Assert.assertArrayEquals(l.toArray(), arr);
    }

}
