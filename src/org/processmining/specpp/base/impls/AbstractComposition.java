package org.processmining.specpp.base.impls;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.traits.ProperlyPrintable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * The abstract base class for compositions.
 * @param <C> candidate type
 * @param <K> type of the internally managed collection
 */
public abstract class AbstractComposition<C extends Candidate, K extends Collection<C>> extends AbstractBaseClass implements CompositionComponent<C>, ProperlyPrintable {

    protected final K candidates;
    private C lastAcceptedCandidate;

    public AbstractComposition(Supplier<K> candidateCollection) {
        this.candidates = candidateCollection.get();
    }

    public C getLastAcceptedCandidate() {
        return lastAcceptedCandidate;
    }

    @Override
    public int size() {
        return candidates.size();
    }

    @Override
    public Set<C> toSet() {
        return ImmutableSet.copyOf(candidates);
    }

    @Override
    public List<C> toList() {
        return ImmutableList.copyOf(candidates);
    }

    @Override
    public Iterator<C> iterator() {
        return candidates.iterator();
    }

    @Override
    public void accept(C candidate) {
        candidates.add(candidate);
        setLastAcceptedCandidate(candidate);
    }

    protected void setLastAcceptedCandidate(C candidate) {
        lastAcceptedCandidate = candidate;
    }

    @Override
    public String toString() {
        return candidates.toString();
    }
}
