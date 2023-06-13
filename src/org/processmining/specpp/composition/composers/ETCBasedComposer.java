package org.processmining.specpp.composition.composers;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MapIterator;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.AbstractComposer;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.config.parameters.ETCBasedComposerParameters;
import org.processmining.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.Factory;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.transitionSystems.PAState;
import org.processmining.specpp.datastructures.transitionSystems.PrefixAutomaton;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;

import java.nio.IntBuffer;
import java.util.*;

public class ETCBasedComposer<I extends AdvancedComposition<Place>> extends AbstractComposer<Place, I, CollectionOfPlaces> {

    // input: log and parameters (rho)
    private final DelegatingDataSource<Log> logSource = new DelegatingDataSource<>();
    private final DelegatingDataSource<ETCBasedComposerParameters> parameters = new DelegatingDataSource<>();

    // resources: Mapping: activity<->transitions, Mapping: activity->ingoing places, markingHistoriesEvaluator
    private final DelegatingDataSource<BidiMap<Activity, Transition>> actTransMapping = new DelegatingDataSource<>();
    private final DelegatingEvaluator<Place, VariantMarkingHistories> markingHistoriesEvaluator = new DelegatingEvaluator<>();
    private final Map<Activity, Set<Place>> activityToIngoingPlaces = new HashMap<>();

    // structures: prefix automaton, activity mappings, marking history cache
    private final PrefixAutomaton prefixAutomaton = new PrefixAutomaton(new PAState());
    private final Map<Activity, Integer> activityToEscapingEdges = new HashMap<>();
    private final Map<Activity, Integer> activityToAllowed = new HashMap<>();
    private final Map<Place, VariantMarkingHistories> markingHistoriesCache = new HashMap<>();

    // attributes: current precision, flag: new addition in this call
    private double currETCPrecision;
    private boolean newAddition = false;

    /**
     * Creates a new ETC-based Composer
     *
     * @param composition Collection of places ("Intermediate Model")
     */
    public ETCBasedComposer(I composition) {
        super(composition, c -> new CollectionOfPlaces(c.toList()));

        globalComponentSystem().require(DataRequirements.RAW_LOG, logSource)
                               .require(DataRequirements.ACT_TRANS_MAPPING, actTransMapping)
                               .require(EvaluationRequirements.PLACE_MARKING_HISTORY, markingHistoriesEvaluator)
                               .require(ParameterRequirements.ETC_BASED_COMPOSER_PARAMETERS, parameters);
    }


    /**
     * Deliberate, whether place should be added to the composition or not
     *
     * @param candidate the candidate to decide acceptance for
     * @return true, if candidate should be added. Otherwise, false.
     */
    @Override
    protected boolean deliberateAcceptance(Place candidate) {
        markingHistoriesCache.put(candidate, markingHistoriesEvaluator.eval(candidate));

        if (!checkPrecisionGain(candidate)) {
            // no decrease in EE(a) for any activity a that was reevaluated
            return false;

        } else {
            // candidate place makes the result more precise -> check for potentially implicit places

            //collect potentially implicit places: places whose set of outgoing activities intersects with the candidate's
            LinkedList<Place> potImpl = new LinkedList<>();

            BitEncodedSet<Transition> candidateOut = candidate.postset();
            Set<Activity> activitiesToReevaluate = new HashSet<>();
            for (Transition t : candidateOut) {
                activitiesToReevaluate.add(actTransMapping.getData().getKey(t));
            }
            for (Activity a : activitiesToReevaluate) {
                potImpl.addAll(activityToIngoingPlaces.get(a));
            }

            //check implicitness and remove
            addToActivityPlacesMapping(candidate); //add candidate to collection of places "test-wise"

            for (Place pPotImpl : potImpl) {
                if (checkImplicitness(pPotImpl)) {
                    revokeAcceptance(pPotImpl);
                }
            }

            removeFromActivityPlacesMapping(candidate);

            newAddition = true;
            return true;
        }
    }

    /**
     * check (tau=1) / approximate (tau<1) whether a place makes the intermediate result sufficiently more precise
     *
     * @param p place
     * @return true, if p is not implicit / sufficiently improves precision. Otherwise, false.
     */
    public boolean checkPrecisionGain(Place p) {

        // collect activities to reevaluate: p's outgoing activities
        BitEncodedSet<Transition> candidateOut = p.postset();
        Set<Activity> activitiesToReevaluate = new HashSet<>();
        for (Transition t : candidateOut) {
            activitiesToReevaluate.add(actTransMapping.getData().getKey(t));
        }

        boolean isMorePrecise = false;

        addToActivityPlacesMapping(p); //add p to collection of places "test-wise"

        Map<Activity, Integer> tmpActivityToEscapingEdges = new HashMap<>(activityToEscapingEdges);
        Map<Activity, Integer> tmpActivityToAllowed = new HashMap<>(activityToAllowed);

        // reevaluate activity mappings: check if adding p constraints any escaping activities
        for (Activity a : activitiesToReevaluate) {
            int[] evalRes = evaluatePrecision(a);
            int newEE = evalRes[0];
            int newAllowed = evalRes[1];

            if (newEE < activityToEscapingEdges.get(a)) {
                isMorePrecise = true;
            }

            tmpActivityToEscapingEdges.put(a, newEE);
            tmpActivityToAllowed.put(a, newAllowed);
        }

        removeFromActivityPlacesMapping(p);

        if (isMorePrecise) {
            // update activity mappings and current precision since we add p
            activityToEscapingEdges.putAll(tmpActivityToEscapingEdges);
            activityToAllowed.putAll(tmpActivityToAllowed);
            currETCPrecision = calcETCPrecision(tmpActivityToEscapingEdges, tmpActivityToAllowed);
            return true;
        }
        return false;
    }

    /**
     * check (tau=1) / approximate (tau<1) whether a place is implicit (gamma=0) / insufficiently constrains precision (gamma>0)
     *
     * @param p place
     * @return true, if p is implicit (gamma=0) / insufficiently constrains precision (gamma>0). Otherwise, false.
     */
    public boolean checkImplicitness(Place p) {

        // collect activities to reevaluate: p's outgoing activities
        BitEncodedSet<Transition> pPotImplOut = p.postset();
        Set<Activity> activitiesToReevaluatePPotImpl = new HashSet<>();
        for (Transition t : pPotImplOut) {
            activitiesToReevaluatePPotImpl.add(actTransMapping.getData().getKey(t));
        }

        removeFromActivityPlacesMapping(p); //remove p to collection of places "test-wise"

        boolean hasEqualValues = true;

        // reevaluate activity mappings: check if removing p allows for more allowed/escaping activities
        for (Activity a : activitiesToReevaluatePPotImpl) {
            int[] evalRes = evaluatePrecision(a);
            int newEE = evalRes[0];
            int newAllowed = evalRes[1];

            if ((newEE != activityToEscapingEdges.get(a)) || newAllowed != activityToAllowed.get(a)) {
                hasEqualValues = false;
                break;
            }
        }

        addToActivityPlacesMapping(p);

        return hasEqualValues;
    }

    /**
     * Executed when candidate is revoked (is implicit (rho=0), insufficiently constrains precision (rho>0)).
     *
     * @param candidate Revoked candidate.
     */
    @Override
    protected void acceptanceRevoked(Place candidate) {
        //update markingHistoriesCache
        markingHistoriesCache.remove(candidate);
        //update ActivityPlaceMapping
        removeFromActivityPlacesMapping(candidate);
    }

    /**
     * Executed when candidate is accepted (its addition makes the intermediate model more precise (by gamma))-
     *
     * @param candidate Accepted candidate.
     */
    @Override
    protected void candidateAccepted(Place candidate) {
        addToActivityPlacesMapping(candidate);
    }

    /**
     * Executed when a candidate is rejected (adding it would not make the intermediate model (sufficiently) more precise
     *
     * @param candidate Rejected candidate.
     */
    @Override
    protected void candidateRejected(Place candidate) {
        //update markingHistoriesCache
        markingHistoriesCache.remove(candidate);
    }

    /**
     * Executed after the (premature) abort of the discovery
     * Prints the (approximate) precision of the final model
     */
    @Override
    public void candidatesAreExhausted() {
    }

    /**
     * Initialize the ETC-based composer: Builds prefix automaton and initial activity mappings.
     */
    @Override
    protected void initSelf() {
        // Build Prefix-Automaton
        Log log = logSource.getData();
        for (IndexedVariant indexedVariant : log) {
            prefixAutomaton.addVariant(indexedVariant.getVariant());
        }

        // Init ActivityPlaceMapping
        MapIterator<Activity, Transition> mapIterator = actTransMapping.get().mapIterator();
        while (mapIterator.hasNext()) {
            Activity a = mapIterator.next();
            activityToIngoingPlaces.put(a, new HashSet<>());
        }

        // Init EscapingEdges
        Set<Activity> activities = actTransMapping.getData().keySet();
        for (Activity a : activities) {
            if (!a.equals(Factory.ARTIFICIAL_START)) {
                int[] evalRes = evaluatePrecision(a);
                int EE = evalRes[0];
                int allowed = evalRes[1];
                activityToEscapingEdges.put(a, EE);
                activityToAllowed.put(a, allowed);
            }
        }
    }

    /**
     * Adds a place test-wise.
     *
     * @param p Place.
     */
    private void addToActivityPlacesMapping(Place p) {
        for (Transition t : p.postset()) {
            Activity a = actTransMapping.get().getKey(t);
            Set<Place> tIn = activityToIngoingPlaces.get(a);
            tIn.add(p);
        }
    }

    /**
     * Removes a place test-wise.
     *
     * @param p Place.
     */
    private void removeFromActivityPlacesMapping(Place p) {
        for (Transition t : p.postset()) {
            Activity a = actTransMapping.get().getKey(t);
            Set<Place> tIn = activityToIngoingPlaces.get(a);
            tIn.remove(p);
        }
    }


    /**
     * Checks whether search can be aborted prematurely.
     *
     * @return true, if search can be aborted. Otherwise, false.
     */
    @Override
    public boolean isFinished() {
        if (newAddition) {
            newAddition = false;
            return checkPrecisionThreshold(parameters.get().getRho());
        } else {
            return false;
        }
    }

    /**
     * Checks whether precision threshold rho has been met
     *
     * @return true, if threshold is reached. Otherwise, false.
     */
    public boolean checkPrecisionThreshold(double p) {
        return currETCPrecision >= p;
    }

    /**
     * Calculates the (approximate) ETC-precision based on the given activity mappings (including/excluding test-wise added/removed places)
     *
     * @param activityToEscapingEdges Mapping from activities to #EscapingEdges
     * @param activityToAllowed       Mapping from activities to #Allowed
     * @return (approximate) ETC-precision
     */
    public double calcETCPrecision(Map<Activity, Integer> activityToEscapingEdges, Map<Activity, Integer> activityToAllowed) {
        int EE = 0;
        for (int i : activityToEscapingEdges.values()) {
            EE += i;
        }
        int allowed = 0;
        for (int i : activityToAllowed.values()) {
            allowed += i;
        }
        //for starting activity:
        allowed += logSource.getData().totalTraceCount();

        return (1 - ((double) EE / allowed));
    }


    /**
     * reevaluates (tau=1) / approximates (tau<1) the activity's mapping entries
     *
     * @param a activity to evaluate
     * @return Integer-array of size two. [0]-#EscapingEdges a, [1]-#Allowed a
     */
    public int[] evaluatePrecision(Activity a) {

        int escapingEdges = 0;
        int allowed = 0;
        Log log = logSource.getData();


        Set<Place> prerequisites = activityToIngoingPlaces.get(a);

        // collect markingHistories
        LinkedList<VariantMarkingHistories> markingHistories = new LinkedList<>();
        for (Place p : prerequisites) {
            markingHistories.add(markingHistoriesCache.get(p));
        }

        //iterate log: variant by variant, activity by activity

        for (IndexedVariant variant : log) {

            int vIndex = variant.getIndex();
            int length = variant.getVariant().getLength();

            PAState logState = prefixAutomaton.getInitial();


            for (int j = 1; j < length * 2; j += 2) {
                int aIndex = ((j + 1) / 2) - 1;

                // update log state
                logState = logState.getTrans(log.getVariant(vIndex).getAt(aIndex)).getPointer();

                boolean isAllowedO = true;

                for (VariantMarkingHistories h : markingHistories) {
                    //check if mh=1 f.a. p in *a --> activity a is allowed
                    IntBuffer buffer = h.getAt(vIndex);
                    int p = buffer.position();

                    if (buffer.get(p + j) == 0) {
                        isAllowedO = false;
                        break;
                    }
                }

                if (isAllowedO) {
                    allowed += log.getVariantFrequency(vIndex);

                    //check if "a" reflected
                    if (!logState.checkForOutgoingAct(a)) {
                        //a not reflected, hence escaping
                        escapingEdges += log.getVariantFrequency(vIndex);
                    }
                }
            }
        }
        return new int[]{escapingEdges, allowed};
    }


}
