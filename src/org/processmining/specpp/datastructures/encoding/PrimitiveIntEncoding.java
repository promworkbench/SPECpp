package org.processmining.specpp.datastructures.encoding;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface PrimitiveIntEncoding extends IntEncoding<Integer> {

    IntStream primitiveDomain();

    @Override
    default Stream<Integer> domain() {
        return primitiveDomain().boxed();
    }

    boolean isIntInDomain(int toEncode);

    @Override
    default boolean isInDomain(Integer toEncode) {
        return isIntInDomain(toEncode);
    }

    int encodeInt(int i);

    default IntStream encodeIntStream(IntStream stream) {
        return stream.map(this::encodeInt);
    }

    int decodeInt(int i);

    default IntStream decodeIntStream(IntStream stream) {
        return stream.map(this::decodeInt);
    }

    @Override
    default Integer encode(Integer item) {
        return encodeInt(item);
    }

    @Override
    default Integer decode(Integer value) {
        return decodeInt(value);
    }


}
