package org.processmining.specpp.datastructures.encoding;

import org.processmining.specpp.traits.Copyable;
import org.processmining.specpp.traits.ProperlyHashable;

import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a subset of the encoding domain of an {@code IntEncoding<T>}.
 * Supports efficient set queries and mutations with other encoded sets, as declared in {@code SetQueries} and {@code MutatingSetOperations}.
 * Primarily used as an ordered subset with compact encodings that essentially map the domain to ordering indices starting at zero.
 *
 * @param <T> the type of items in this set
 */
public class BitEncodedSet<T> implements EncodedSet<T, Integer>, ProperlyHashable, SetQueries<BitEncodedSet<T>>, MutatingSetOperations<BitEncodedSet<T>>, Copyable<BitEncodedSet<T>> {


    protected final IntEncoding<T> encoding;
    /**
     * The underlying bitmask on the range of the encoding that indicates set membership of the corresponding domain items.
     */
    protected final BitMask set;

    public BitEncodedSet(IntEncoding<T> encoding, BitMask set) {
        this.encoding = encoding;
        this.set = set;
    }

    protected BitEncodedSet(IntEncoding<T> encoding) {
        this(encoding, new BitMask());
    }

    public static <T> BitEncodedSet<T> empty(IntEncoding<T> enc) {
        return new BitEncodedSet<>(enc);
    }

    public BitEncodedSet<T> reencode(IntEncoding<T> newEncoding) {
        BitMask mask = new BitMask();
        set.stream()
           .mapToObj(encoding::decode)
           .filter(newEncoding::isInDomain)
           .mapToInt(newEncoding::encode)
           .forEach(mask::set);
        return new BitEncodedSet<>(newEncoding, mask);
    }

    public IntEncoding<T> getEncoding() {
        return encoding;
    }

    public BitMask getBitMask() {
        return set;
    }

    private void checkEnc(BitEncodedSet<T> other) {
        if (!hasSameEnc(other)) throw new InconsistentEncodingException();
    }

    private boolean hasSameEnc(BitEncodedSet<T> other) {
        return encoding == other.encoding || encoding.equals(other.getEncoding());
    }

    @Override
    public void intersection(BitEncodedSet<T> other) {
        if (hasSameEnc(other)) set.and(other.set);
        else {
            Iterator<T> it = streamItems().iterator();
            while (it.hasNext()) {
                T next = it.next();
                if (!other.contains(next)) remove(next);
            }
        }
    }

    @Override
    public void union(BitEncodedSet<T> other) {
        if (hasSameEnc(other)) set.or(other.set);
        else {
            other.streamItems().filter(encoding::isInDomain).forEach(this::add);
        }
    }

    @Override
    public void setminus(BitEncodedSet<T> other) {
        if (hasSameEnc(other)) set.andNot(other.set);
        else {
            other.streamItems().filter(encoding::isInDomain).forEach(this::remove);
        }
    }

    public boolean containsIndex(int index) {
        return index >= 0 && set.get(index);
    }

    @Override
    public boolean contains(T item) {
        return encoding.isInDomain(item) && set.get(encoding.encode(item));
    }

    /**
     * Computes a bitmask of the unset indices between the {@code k}-th largest and the {@code k-1}-th largest set indices.
     *
     * @param k
     * @return
     * @see #kMaxRange(int)
     * @see #kMaxIndex(int)
     */
    public BitMask kMaxRangeMask(int k) {
        BitMask mask = new BitMask(maxSize());
        if (0 < k && k <= cardinality()) mask.set(kMaxIndex(k) + 1, kMaxIndex(k - 1));
        else if (k == cardinality() + 1) mask.set(kMaxIndex(k) + 1, maxSize());
        return mask;
    }

    /**
     * @return the minimal set index in the underlying bitmask
     * @see #set
     */
    public int minimalIndex() {
        return kMinIndex(1);
    }

    /**
     * @return the maximal set index in the underlying bitmask
     * @see #set
     */
    public int maximalIndex() {
        return kMaxIndex(1);
    }

    /**
     * @return the minimal item contained in this subset
     */
    public T minimalElement() {
        return encoding.decode(minimalIndex());
    }

    /**
     * @return the maximal item contained in this subset
     */
    public T maximalElement() {
        return encoding.decode(maximalIndex());
    }

    // this is clamped. watch out for the semantics. same for kMaxIndex

    /**
     * Computes the {@code k}-th smallest set index, aka the smallest item in this subset when the encoding is interpreted as an ordering.
     * Returns {@code -1} for {@code k<1} and {@code maxSize()} for {@code k} exceeding the cardinality of the underlying bitmask {@code set}.
     *
     * @param k
     * @return
     * @see #maxSize()
     * @see #set
     */
    public int kMinIndex(int k) {
        if (k <= 0) return -1;
        else if (k > cardinality()) return maxSize();
        return set.kMinIndex(k);
    }

    /**
     * Computes the {@code k}-th largest set index, aka the largest item in this subset when the encoding is interpreted as an ordering.
     * Returns {@code maxSize()} for {@code k<1} and {@code -1} for {@code k} exceeding the cardinality of the underlying bitmask {@code set}.
     *
     * @param k
     * @return
     * @see #maxSize()
     * @see #set
     */
    public int kMaxIndex(int k) {
        if (k <= 0) return maxSize();
        else if (k > cardinality()) return -1;
        return set.kMaxIndex(k);
    }

    /**
     * @param k
     * @return the corresponding item to the {@code kMinIndex(k)}
     * @see #kMinIndex(int)
     */
    public T kMinItem(int k) {
        int index = kMinIndex(k);
        return index >= 0 ? encoding.decode(index) : null;
    }

    /**
     * @param k
     * @return the corresponding item to the {@code kMaxIndex(k)}
     * @see #kMaxIndex(int)
     */
    public T kMaxItem(int k) {
        int index = kMaxIndex(k);
        return index >= 0 ? encoding.decode(index) : null;
    }

    /**
     * {@code kMaxRange()} in reverse.
     *
     * @param k
     * @return
     * @see #kMaxRange(int)
     */
    public int kMinRange(int k) {
        if (k <= 0 || k > cardinality()) return 0;
        return kMinIndex(k - 1) - kMinIndex(k);
    }

    /**
     * Computes the number of empty spots (zeros) in the encoding between the {@code k}-th and {@code k-1}-th largest set indices in the underlying {@code set}.
     * With the encoding read as an ordering, this is the number of currently not contained items between the contained {@code k}-th largest item and its largest contained predecessor.
     * The sum of all ranges over {@code 0 to maxIndex()} is exactly the number of unset indices in the underlying {@code bitset}, i.e. the number of missing items in this subset.
     * Refer to the following example:
     * <pre>{@code
     * set = 0011001, c = |set| = 3, L = maxIndex(set) = 6
     * k   kMaxRange
     * 0   0
     * 1   0
     * 2   2
     * 3   0
     * 4   2
     * 5   0
     * 6   0
     * invariant: sum_k=0^L( kMaxRange(k) ) = L - c, i.e. #of zeros in bitset }</pre>
     *
     * @param k
     * @return
     */
    public int kMaxRange(int k) {
        if (0 <= k || k > cardinality() + 1) return 0;
        else if (k == cardinality() + 1) return maxSize() - 1 - kMaxIndex(k);
        else return kMaxIndex(k - 1) - kMaxIndex(k) - 1;
    }

    @Override
    public boolean add(T item) {
        return encoding.isInDomain(item) && addIndex(encoding.encode(item));
    }

    @SafeVarargs
    @Override
    public final void addAll(T... items) {
        for (T item : items) {
            add(item);
        }
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * @return the maximum size this subset may grow to, as specified by the encoding, i.e. its range size.
     */
    public int maxSize() {
        return encoding.size();
    }

    /**
     * Adds the item corresponding to this index if {@code index} is contained in the encoding range.
     *
     * @param index the index to be set
     * @return whether the set changed as a result, i.e. whether the index was unset before
     */
    public boolean addIndex(int index) {
        if (!encoding.isIntInRange(index)) return false;
        boolean temp = set.get(index);
        set.set(index);
        return !temp;
    }

    /**
     * Removes the specified item from this subset.
     *
     * @param item the item to be removed
     * @return whether this set changed as a result, i.e. whether the item was contained before
     */
    @Override
    public boolean remove(T item) {
        return removeIndex(encoding.encode(item));
    }

    /**
     * Removes the item corresponding to this index if {@code index} is contained in the encoding range.
     *
     * @param index the index to be unset
     * @return whether the set changed as a result, i.e. whether the index was set before
     */
    public boolean removeIndex(int index) {
        if (!encoding.isIntInRange(index)) return false;
        boolean temp = set.get(index);
        set.clear(index);
        return temp;
    }

    /**
     * @return the cardinality of this subset
     */
    public int cardinality() {
        return set.cardinality();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BitEncodedSet<?> that = (BitEncodedSet<?>) o;

        if (!encoding.equals(that.encoding)) return false;
        return set.equals(that.set);
    }

    @Override
    public int hashCode() {
        int result = encoding.hashCode();
        result = 31 * result + set.hashCode();
        return result;
    }

    /**
     * Copies this subset. As encoding are immutable this object's encoding is referenced in the copy instead of deep copied itself.
     *
     * @return a copy of this subset
     */
    @Override
    public BitEncodedSet<T> copy() {
        return new BitEncodedSet<>(encoding, (BitMask) set.clone());
    }

    @Override
    public Iterator<T> iterator() {
        return streamItems().iterator();
    }

    /**
     * @return a stream of all items contained in this set
     */
    public Stream<T> streamItems() {
        return set.stream().mapToObj(encoding::decode);
    }

    /**
     * @return an {@code IntStream} of all set indices in the underlying bitmask.
     * @see #set
     */
    public IntStream streamIndices() {
        return set.stream();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = set.nextSetBit(0); i >= 0 && i < set.length(); i = set.nextSetBit(i + 1)) {
            sb.append(encoding.decode(i).toString());
            if (set.nextSetBit(i + 1) > 0) sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }


    public void clearMask(BitMask bitMask) {
        set.setminus(bitMask);
    }

    public void retainMask(BitMask bitMask) {
        set.intersection(bitMask);
    }

    public void addMask(BitMask bitMask) {
        if (bitMask.previousSetBit(bitMask.length()) <= maxSize()) set.union(bitMask);
    }

    @Override
    public boolean intersects(BitEncodedSet<T> other) {
        if (hasSameEnc(other)) return set.intersects(other.getBitMask());
        else return streamItems().anyMatch(other::contains);
    }

    @Override
    public boolean setEquality(BitEncodedSet<T> other) {
        if (hasSameEnc(other)) return set.setEquality(other.getBitMask());
        else return streamItems().allMatch(other::contains) && other.streamItems().allMatch(this::contains);
    }

    @Override
    public boolean isSubsetOf(BitEncodedSet<T> other) {
        if (hasSameEnc(other)) return set.isSubsetOf(other.getBitMask());
        else return streamItems().allMatch(other::contains);
    }

    @Override
    public boolean isSupersetOf(BitEncodedSet<T> other) {
        if (hasSameEnc(other)) return set.isSupersetOf(other.getBitMask());
        else return other.streamItems().allMatch(this::contains);
    }
}
