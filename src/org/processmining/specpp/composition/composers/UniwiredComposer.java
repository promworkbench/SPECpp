package org.processmining.specpp.composition.composers;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.base.impls.AbstractPostponingComposer;
import org.processmining.specpp.base.impls.CandidateConstraint;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.ContainerUtils;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingObserver;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.constraints.AddWiredPlace;
import org.processmining.specpp.datastructures.tree.constraints.RemoveWiredPlace;
import org.processmining.specpp.datastructures.tree.nodegen.UnWiringMatrix;
import org.processmining.specpp.evaluation.heuristics.CandidateScore;
import org.processmining.specpp.proposal.ProposerSignal;
import org.processmining.specpp.util.JavaTypingUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class UniwiredComposer<I extends AdvancedComposition<Place>, R extends Result> extends AbstractPostponingComposer<Place, I, R, CandidateConstraint<Place>> {

    protected final ArrayList<Place> collectedPlaces;
    protected final DelegatingObserver<ProposerSignal> proposerSignalsIn = new DelegatingObserver<>();
    protected final DelegatingDataSource<IntEncodings<Transition>> transitionEncodings = new DelegatingDataSource<>();
    protected final DelegatingDataSource<HeuristicStrategy<Place, CandidateScore>> orderingHeuristic = new DelegatingDataSource<>();
    protected UnWiringMatrix wiringMatrix;
    protected int currentTreeLevel;
    protected final DelegatingDataSource<Integer> treeLevelSource = new DelegatingDataSource<>(() -> currentTreeLevel);
    private HeuristicStrategy<Place, CandidateScore> heuristic;

    public UniwiredComposer(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
        collectedPlaces = new ArrayList<>();
        currentTreeLevel = 0;

        globalComponentSystem().require(DataRequirements.ENC_TRANS, transitionEncodings)
                               .require(SupervisionRequirements.observable("external.constraints.wiring", JavaTypingUtils.castClass(CandidateConstraint.class)), ContainerUtils.observeResults(this::addConstraint))
                               .require(DataRequirements.dataSource("tree.current_level", Integer.class), treeLevelSource)
                               .require(DataRequirements.dataSource("heuristics.place.df", JavaTypingUtils.castClass(HeuristicStrategy.class)), orderingHeuristic);
        localComponentSystem().require(SupervisionRequirements.observer("proposer.signals.in", ProposerSignal.class), proposerSignalsIn)
                              .require(SupervisionRequirements.observable("composition.constraints.wiring", JavaTypingUtils.castClass(CandidateConstraint.class)), ContainerUtils.observeResults(this::addConstraint));

    }

    private void addConstraint(CandidateConstraint<Place> constraint) {
        if (constraint instanceof AddWiredPlace) wiringMatrix.wire(constraint.getAffectedCandidate());
        else if (constraint instanceof RemoveWiredPlace) wiringMatrix.unwire(constraint.getAffectedCandidate());
    }

    @Override
    protected void initSelf() {
        heuristic = orderingHeuristic.getData();
        IntEncodings<Transition> encodings = transitionEncodings.getData();
        wiringMatrix = new UnWiringMatrix(encodings);

    }

    @Override
    protected CandidateDecision deliberateCandidate(Place candidate) {
        if (candidate.size() > currentTreeLevel) {
            currentTreeLevel = candidate.size();
            iteratePostponedCandidatesUntilNoChange();
        }
        return CandidateDecision.Postpone;
    }

    @Override
    protected boolean iteratePostponedCandidates() {
        if (collectedPlaces.isEmpty()) return false;
        collectedPlaces.sort(Comparator.comparing(heuristic::computeHeuristic, heuristic.heuristicValuesComparator()));

        for (Place place : collectedPlaces) {
            if (wiringMatrix.isWired(place)) rejectCandidate(place);
            else acceptCandidate(place);
        }
        collectedPlaces.clear();
        return true;
    }


    @Override
    protected void postponeDecision(Place candidate) {
        collectedPlaces.add(candidate);
    }

    @Override
    protected void rejectCandidate(Place candidate) {

    }

    @Override
    protected void discardCandidate(Place candidate) {

    }

    @Override
    public Class<CandidateConstraint<Place>> getPublishedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
    }
}
