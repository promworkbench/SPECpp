package org.processmining.specpp.datastructures.encoding;

import org.processmining.specpp.traits.ProperlyPrintable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IntEncodings<T> implements ProperlyPrintable {

    private final IntEncoding<T> presetEncoding, postsetEncoding;

    public IntEncodings(IntEncoding<T> presetEncoding, IntEncoding<T> postsetEncoding) {
        this.presetEncoding = presetEncoding;
        this.postsetEncoding = postsetEncoding;
    }

    public static <A, B> IntEncoding<A> mapEncoding(IntEncoding<B> encoding, Map<A, B> mapping) {
        Map<A, Integer> mappedEncoding = mapping.entrySet()
                                                .stream()
                                                .filter(e -> encoding.isInDomain(e.getValue()))
                                                .collect(Collectors.toMap(Map.Entry::getKey, e -> encoding.encode(e.getValue())));
        return HashmapEncoding.copyOf(mappedEncoding);
    }

    public static <A, B> IntEncodings<A> mapEncodings(IntEncodings<B> encodings, Map<A, B> mapping) {
        IntEncoding<B> presetEncoding = encodings.getPresetEncoding();
        IntEncoding<B> postsetEncoding = encodings.getPostsetEncoding();
        return new IntEncodings<>(mapEncoding(presetEncoding, mapping), mapEncoding(postsetEncoding, mapping));
    }

    public Set<T> domainUnion() {
        HashSet<T> result = new HashSet<>();
        presetEncoding.domain().forEach(result::add);
        postsetEncoding.domain().forEach(result::add);
        return result;
    }

    public Set<T> domainIntersection() {
        HashSet<T> result = new HashSet<>();
        presetEncoding.domain().filter(postsetEncoding::isInDomain).forEach(result::add);
        return result;
    }

    public IntEncoding<T> unionizedEncoding() {
        return HashmapEncoding.ofList(new ArrayList<>(domainUnion()));
    }

    public IntEncoding<T> getPresetEncoding() {
        return presetEncoding;
    }

    public IntEncoding<T> getPostsetEncoding() {
        return postsetEncoding;
    }

    public IntEncoding<T> pre() {
        return getPresetEncoding();
    }

    public IntEncoding<T> post() {
        return getPostsetEncoding();
    }

    @Override
    public String toString() {
        return "{presetEncoding=" + presetEncoding + ", postsetEncoding=" + postsetEncoding + "}";
    }

}
