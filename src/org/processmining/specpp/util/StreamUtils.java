package org.processmining.specpp.util;

import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {

    public static Predicate<IntStream> intStreamPredicate(IntPredicate runningPredicate, IntPredicate postPredicate) {
        return (IntStream is) -> {
            PrimitiveIterator.OfInt iterator = is.iterator();
            if (!iterator.hasNext()) return true;
            int item;
            do {
                item = iterator.next();
                if (!runningPredicate.test(item)) return false;
            } while (iterator.hasNext());
            return postPredicate.test(item);
        };
    }

    public static String stringify(IntStream is) {
        return "[" + is.mapToObj(Integer::toString).collect(Collectors.joining(" ")) + "]";
    }

    public static <T> String stringify(Stream<T> stream) {
        return "[" + stream.map(o -> o instanceof IntStream ? stringify((IntStream) o) : Objects.toString(o))
                           .collect(Collectors.joining(",")) + "]";
    }

    public static <T> void print(Stream<T> stream) {
        System.out.println(stringify(stream));
    }

    public static <T> Stream<T> interleaveStreams(Stream<? extends T> a, Stream<? extends T> b) {
        Spliterator<? extends T> spA = a.spliterator(), spB = b.spliterator();
        long s = spA.estimateSize() + spB.estimateSize();
        if (s < 0) s = Long.MAX_VALUE;
        int ch = spA.characteristics() & spB.characteristics() & (Spliterator.NONNULL | Spliterator.SIZED);
        ch |= Spliterator.ORDERED;

        return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(s, ch) {
            Spliterator<? extends T> sp1 = spA, sp2 = spB;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                Spliterator<? extends T> sp = sp1;
                if (sp.tryAdvance(action)) {
                    sp1 = sp2;
                    sp2 = sp;
                    return true;
                }
                return sp2.tryAdvance(action);
            }
        }, false);
    }

    public static IntStream interleaveIntStreams(IntStream a, IntStream b) {
        Spliterator.OfInt spA = a.spliterator(), spB = b.spliterator();
        long s = spA.estimateSize() + spB.estimateSize();
        if (s < 0) s = Long.MAX_VALUE;
        int ch = spA.characteristics() & spB.characteristics() & (Spliterator.NONNULL | Spliterator.SIZED);
        ch |= Spliterator.ORDERED;

        return StreamSupport.intStream(new Spliterators.AbstractIntSpliterator(s, ch) {
            OfInt sp1 = spA, sp2 = spB;

            @Override
            public boolean tryAdvance(IntConsumer action) {
                OfInt sp = sp1;
                if (sp.tryAdvance(action)) {
                    sp1 = sp2;
                    sp2 = sp;
                    return true;
                }
                return sp2.tryAdvance(action);
            }
        }, false);
    }

    public static boolean streamsEqual(IntStream leftStream, IntStream rightStream) {
        PrimitiveIterator.OfInt leftIt = leftStream.iterator(), rightIt = rightStream.iterator();
        while (leftIt.hasNext() && rightIt.hasNext()) {
            if (leftIt.nextInt() != rightIt.nextInt()) return false;
        }
        return !leftIt.hasNext() && !rightIt.hasNext();
    }

}
