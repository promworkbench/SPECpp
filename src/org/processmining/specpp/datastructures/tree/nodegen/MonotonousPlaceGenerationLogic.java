package org.processmining.specpp.datastructures.tree.nodegen;

import org.apache.commons.collections4.IteratorUtils;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.ContainerUtils;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.specpp.datastructures.tree.base.PlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.constraints.*;
import org.processmining.specpp.datastructures.tree.heuristic.SubtreeCutoffConstraint;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.util.Tuple2;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Contains the entire logic for generating child (and in the future parent) local nodes.
 * Relies only on the state of a given node and the global preset- & postset transition orderings to deterministically compute its children while respecting all received constraints.
 * Guarantees to generate all possible nodes that satisfy the incoming constraints, provided that the constraints monotonically shrink the set of future nodes.
 * That is, if constraints loosen the requirements and allow previously excluded nodes to be generated, these may not be correctly returned.
 */
public class MonotonousPlaceGenerationLogic extends PlaceGenerationLogic {

    protected final List<Tuple2<Class<? extends GenerationConstraint>, Consumer<GenerationConstraint>>> constraintHandlers;

    public enum ExpansionType {
        Postset, Preset

    }

    protected final IntEncodings<Transition> transitionEncodings;
    protected final List<PotentialExpansionsFilter> potentialExpansionFilters;
    protected final List<ExpansionStopper> expansionStoppers;

    public static class Builder extends ComponentSystemAwareBuilder<PlaceGenerationLogic> {

        protected final DelegatingDataSource<IntEncodings<Transition>> transitionEncodings = new DelegatingDataSource<>();
        protected final DelegatingDataSource<PlaceGeneratorParameters> parameters = new DelegatingDataSource<>();


        public Builder() {
            globalComponentSystem().require(DataRequirements.ENC_TRANS, transitionEncodings)
                                   .require(ParameterRequirements.PLACE_GENERATOR_PARAMETERS, parameters);
        }

        @Override
        public MonotonousPlaceGenerationLogic buildIfFullySatisfied() {
            return new MonotonousPlaceGenerationLogic(transitionEncodings.getData(), parameters.getData());
        }

    }

    public MonotonousPlaceGenerationLogic(IntEncodings<Transition> transitionEncodings) {
        this(transitionEncodings, PlaceGeneratorParameters.getDefault());
    }

    /**
     * Configures this generator's constraint handling. If specified in {@code parameters}, handlers for the following constraint classes are added dynamically.
     * Subclassing implementations can define their own handlers, which in turn may use new {@code PotentialExpansionFilter}s and {@code ExpansionStopper}s.
     * The following depicts the default flow.
     * <pre>
     *      {@code DepthConstraint} into the {@code depthLimiter}
     *      {@code WiringConstraint} into the {@code wiringTester}
     *      {@code BlacklistTransitionConstraint} into the {@code transitionBlacklister}
     *      {@code SubTreeCutoffConstraint} into {@code directly applied to potentialChildren in the node state}
     * </pre>
     *
     * @param transitionEncodings
     * @param parameters
     */
    public MonotonousPlaceGenerationLogic(IntEncodings<Transition> transitionEncodings, PlaceGeneratorParameters parameters) {
        this.transitionEncodings = transitionEncodings;
        this.constraintHandlers = new LinkedList<>();
        this.potentialExpansionFilters = new LinkedList<>();
        this.expansionStoppers = new LinkedList<>();

        DepthLimiter depthLimiter = new DepthLimiter(parameters.getMaxTreeDepth());
        expansionStoppers.add(depthLimiter);
        if (parameters.isAcceptSubtreeCutoffConstraints()) {
            constraintHandlers.add(new ImmutableTuple2<>(SubtreeCutoffConstraint.class, this::handleCullChildrenConstraint));
        }
        if (parameters.isAcceptWiringConstraints()) {
            WiringTester wiringTester = new UnWiringMatrix(transitionEncodings);
            potentialExpansionFilters.add(wiringTester);
            expansionStoppers.add(wiringTester);
            constraintHandlers.add(new ImmutableTuple2<>(WiringConstraint.class, c -> handleWiringConstraint(wiringTester, c)));
        }
        if (parameters.isAcceptTransitionBlacklistingConstraints()) {
            TransitionBlacklister transitionBlacklister = new TransitionBlacklister(transitionEncodings);
            potentialExpansionFilters.add(transitionBlacklister);
            constraintHandlers.add(new ImmutableTuple2<>(BlacklistTransition.class, c -> handleTransitionBlacklistingConstraint(transitionBlacklister, c)));
        }
        if (parameters.isAcceptDepthConstraints()) {
            constraintHandlers.add(new ImmutableTuple2<>(DepthConstraint.class, c -> handleDepthConstraint(depthLimiter, c)));
        }

        localComponentSystem().require(SupervisionRequirements.observable(SupervisionRequirements.regex("proposer\\.constraints.*"), getAcceptedConstraintClass()), ContainerUtils.observeResults(this));
    }

    @Override
    protected void initSelf() {

    }

    /**
     * @return an empty root node corresponding to the place {@code (∅|∅)} without any existing children
     */
    @Override
    public PlaceNode generateRoot() {
        BitEncodedSet<Transition> preset = BitEncodedSet.empty(transitionEncodings.pre());
        BitEncodedSet<Transition> postset = BitEncodedSet.empty(transitionEncodings.post());
        Place place = Place.of(preset, postset);
        BitMask preMask = canHavePresetChildren(place) ? getStaticPotentialExpansions(preset) : new BitMask();
        BitMask postMask = canHavePostsetChildren(place) ? getStaticPotentialExpansions(postset) : new BitMask();
        return PlaceNode.root(place, PlaceState.withPotentialExpansions(preMask, postMask), this);
    }

    @Override
    public PlaceNode generateChild(PlaceNode parent) {
        Pair<BitMask> potentialExpansions = computePotentialExpansions(parent);
        return makeChild(parent, potentialExpansions, !potentialExpansions.second()
                                                                          .isEmpty() ? ExpansionType.Postset : ExpansionType.Preset);
    }

    /**
     * Creates the next child node to the given parent according to the potential expansion sets.
     * Mutates the internal state of the parent to mark the child's existence.
     * The child's potential expansions are set equal to its parent's minus itself, unless the parent was restricted in from pre/postset expansion completely.
     * Then, it will recalculate the potential expansions statically based on the child place.
     *
     * @param parent              the parent node whose next child is to be generated
     * @param potentialExpansions the pair of potential (preset, postset)-expansions
     * @param expansionType       whether to expand the {@code ExpansionType.Preset} or {@code ExpansionType.Postset}
     * @return the generated child node
     */
    protected PlaceNode makeChild(PlaceNode parent, Pair<BitMask> potentialExpansions, ExpansionType expansionType) {
        BitMask relevant = expansionType == ExpansionType.Postset ? potentialExpansions.second() : potentialExpansions.first();
        assert !relevant.isEmpty();
        int i = relevant.nextSetBit(0);
        Place parentPlace = parent.getPlace();
        BitEncodedSet<Transition> presetCopy = parentPlace.preset().copy(), postsetCopy = parentPlace.postset().copy();
        if (expansionType == ExpansionType.Postset) postsetCopy.addIndex(i);
        else presetCopy.addIndex(i);
        parent.getState().getActualExpansions(expansionType).set(i);
        parent.getState().getPotentialExpansions(expansionType).clear(i);
        relevant.clear(i);

        Place childPlace = new Place(presetCopy, postsetCopy);
        PlaceState childState = makeChildState(potentialExpansions, parentPlace, childPlace);

        return parent.makeChild(childPlace, childState);
    }

    protected PlaceState makeChildState(Pair<BitMask> potentialExpansions, Place parentPlace, Place childPlace) {
        BitMask preMask = canHavePresetChildren(childPlace) ? getStaticPotentialExpansions(childPlace.preset()) : new BitMask();
        BitMask postMask = canHavePostsetChildren(childPlace) ? getStaticPotentialExpansions(childPlace.postset()) : new BitMask();
        return PlaceState.withPotentialExpansions(preMask, postMask);
    }

    /**
     * @param parent
     * @return whether {@code parent} has any possible children as computed by {@code computePotentialExpansions()}
     * @see #computePotentialExpansions(PlaceNode)
     */
    @Override
    public boolean hasChildrenLeft(PlaceNode parent) {
        Pair<BitMask> possibleExpansions = computePotentialExpansions(parent);
        return !possibleExpansions.first().isEmpty() || !possibleExpansions.second().isEmpty();
    }

    /**
     * Computes potential preset and postset expansions using all available constraints.
     * Internally updates the queried node state with the computed result.
     * By design of the constraint system, potential expansions are monotonically decreasing subsets.
     *
     * @param parent node whose possible expansions are to be computes for
     * @return pair(presetExpansions, postsetExpansions)
     */
    protected Pair<BitMask> computePotentialExpansions(PlaceNode parent) {
        PlaceState state = parent.getState();

        if (expansionStoppers.stream().anyMatch(es -> es.notAllowedToExpand(parent))) {
            cullChildren(parent, ExpansionType.Preset);
            cullChildren(parent, ExpansionType.Postset);
            return new ImmutablePair<>(new BitMask(), new BitMask());
        } else {
            Place place = parent.getPlace();
            BitMask possiblePresetExpansions = new BitMask(), possiblePostsetExpansions = new BitMask();
            if (canHavePostsetChildren(place))
                possiblePostsetExpansions = computeFilteredPotentialExpansions(place, state, ExpansionType.Postset);
            if (canHavePresetChildren(place))
                possiblePresetExpansions = computeFilteredPotentialExpansions(place, state, ExpansionType.Preset);

            return new ImmutablePair<>(possiblePresetExpansions, possiblePostsetExpansions);
        }
    }


    /**
     * Computes potential expansions for the specified expansion type.
     * Mutates the state it is querying.
     *
     * @param place
     * @param state         NodeState which is queried and updated
     * @param expansionType the expansion type
     * @return potential expansions represented by a bitmask
     */
    protected BitMask computeFilteredPotentialExpansions(Place place, PlaceState state, ExpansionType expansionType) {
        BitMask potentialExpansions = expansionType == ExpansionType.Postset ? state.getPotentialPostsetExpansions() : state.getPotentialPresetExpansions();

        for (PotentialExpansionsFilter filter : potentialExpansionFilters) {
            potentialExpansions = filter.filterPotentialSetExpansions(place, potentialExpansions, expansionType);
        }

        return potentialExpansions.copy();
    }

    /**
     * The potential expansions statically determined solely by the transition set ordering.
     * All transitions greater than the maximum of {@code transitions} are potential expansions.
     *
     * @param transitions ordered subset of transitions
     * @return potential expansions represented by a bitmask
     */
    protected BitMask getStaticPotentialExpansions(BitEncodedSet<Transition> transitions) {
        return transitions.kMaxRangeMask(1);
    }

    /**
     * @param place
     * @return whether {@code place} is allowed to have postset expansion children
     */
    protected boolean canHavePostsetChildren(Place place) {
        return place.preset().cardinality() > 0 || place.postset().cardinality() == 0;
    }

    /**
     * @param place
     * @return whether {@code place} is allowed to have preset expansion children
     */
    protected boolean canHavePresetChildren(Place place) {
        return place.postset().cardinality() == 1;
    }

    /**
     * Clears the potential expansions of type {@code expansionType} of PlaceNode {@code node}, thus cutting off the corresponding subtree.
     *
     * @param node
     * @param expansionType
     */
    public void cullChildren(PlaceNode node, ExpansionType expansionType) {
        BitMask potentialExpansions = node.getState().getPotentialExpansions(expansionType);
        potentialExpansions.clear();
    }

    /**
     * The number of, at this point, potential children as computed by {@code potentialChildren}.
     *
     * @param parent
     * @return
     * @see #potentialFutureChildren(PlaceNode)
     */
    @Override
    public int potentialChildrenCount(PlaceNode parent) {
        Pair<BitMask> pair = computePotentialExpansions(parent);
        return pair.first().cardinality() + pair.second().cardinality();
    }

    /**
     * Provides a lazily computed iterator of at this point considered potential children.
     * It may be used by tree expansion heuristics.
     *
     * @param parent
     * @return
     */
    @Override
    public Iterable<PlaceNode> potentialFutureChildren(PlaceNode parent) {
        Place place = parent.getPlace();
        PlaceState state = parent.getState();

        Pair<BitMask> pair = computePotentialExpansions(parent);

        Stream<PlaceNode> postsetExpansionsStream = pair.second().stream().mapToObj(i -> {
            BitEncodedSet<Transition> preCopy = place.preset().copy();
            BitEncodedSet<Transition> postCopy = place.postset().copy();
            postCopy.addIndex(i);
            BitMask preExp = state.getPotentialPresetExpansions().copy();
            BitMask postExp = state.getPotentialPostsetExpansions().copy();
            postExp.clear(i);
            return parent.makeChild(Place.of(preCopy, postCopy), PlaceState.withPotentialExpansions(preExp, postExp));
        });

        Stream<PlaceNode> presetExpansionsStream = pair.first().stream().mapToObj(i -> {
            BitEncodedSet<Transition> preCopy = place.preset().copy();
            BitEncodedSet<Transition> postCopy = place.postset().copy();
            preCopy.addIndex(i);
            BitMask preExp = state.getPotentialPresetExpansions().copy();
            BitMask postExp = state.getPotentialPostsetExpansions().copy();
            preExp.clear(i);
            return parent.makeChild(Place.of(preCopy, postCopy), PlaceState.withPotentialExpansions(preExp, postExp));
        });

        return IteratorUtils.asIterable(IteratorUtils.chainedIterator(postsetExpansionsStream.iterator(), presetExpansionsStream.iterator()));
    }


    /**
     * Receives and internally applies selected generation constraints via at instantiation assigned constraint handlers.
     *
     * @param constraint the received generation constraint
     */
    @Override
    public void acceptConstraint(GenerationConstraint constraint) {
        for (Tuple2<Class<? extends GenerationConstraint>, Consumer<GenerationConstraint>> tuple2 : constraintHandlers) {
            Class<? extends GenerationConstraint> constraintClass = tuple2.getT1();
            if (constraintClass.isAssignableFrom(constraint.getClass())) {
                tuple2.getT2().accept(constraint);
                break;
            }
        }
    }

    @Override
    public Class<GenerationConstraint> getAcceptedConstraintClass() {
        return GenerationConstraint.class;
    }

    protected void handleTransitionBlacklistingConstraint(TransitionBlacklister transitionBlacklister, GenerationConstraint constraint) {
        transitionBlacklister.blacklist(((BlacklistTransition) constraint).getTransition());
    }

    protected void handleWiringConstraint(WiringTester wiringTester, GenerationConstraint constraint) {
        if (constraint instanceof AddWiredPlace) wiringTester.wire(((AddWiredPlace) constraint).getAffectedCandidate());
        if (constraint instanceof RemoveWiredPlace)
            wiringTester.unwire(((RemoveWiredPlace) constraint).getAffectedCandidate());
    }

    protected void handleDepthConstraint(DepthLimiter depthLimiter, GenerationConstraint constraint) {
        depthLimiter.updateToMinimum(((DepthConstraint) constraint).getDepthLimit());
    }


    protected void handleCullChildrenConstraint(GenerationConstraint constraint) {
        if (constraint instanceof CullPostsetChildren)
            cullChildren(((CullPostsetChildren) constraint).getAffectedNode(), ExpansionType.Postset);
        else if (constraint instanceof CullPresetChildren)
            cullChildren(((CullPresetChildren) constraint).getAffectedNode(), ExpansionType.Preset);
    }


}
