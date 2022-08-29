package org.processmining.specpp.traits;

import java.util.stream.Stream;

public interface Streamable<T> {

    Stream<T> stream();

}
