import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.BidiMap;
import org.junit.Assert;
import org.junit.Test;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.evaluation.EvaluatorCollection;
import org.processmining.specpp.componenting.supervision.FulfilledObservableRequirement;
import org.processmining.specpp.componenting.supervision.ObservableRequirement;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.datastructures.encoding.*;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.*;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.BiDiTree;
import org.processmining.specpp.datastructures.tree.base.impls.*;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.UnWiringMatrix;
import org.processmining.specpp.datastructures.util.Label;
import org.processmining.specpp.datastructures.util.RegexLabel;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.datastructures.vectorization.IVSComputations;
import org.processmining.specpp.datastructures.vectorization.IntVectorStorage;
import org.processmining.specpp.datastructures.vectorization.VMHComputations;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;
import org.processmining.specpp.evaluation.fitness.AbstractFullFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.MarkingHistoryBasedFitnessEvaluator;
import org.processmining.specpp.headless.SampleData;
import org.processmining.specpp.postprocessing.SelfLoopPlaceMerger;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.preprocessing.orderings.*;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {


    @Test
    public void transitionsets() {

        Transition start = new Transition("\u25B7");
        Transition end = new Transition("\u2610");
        Transition a = new Transition("a");
        Transition b = new Transition("b");
        Transition c = new Transition("c");
        Transition d = new Transition("d");

        Set<Transition> presetTransitions = Sets.newHashSet(start, a, b, c, d);
        Set<Transition> postsetTransitions = Sets.newHashSet(a, b, c, d, end);

        Comparator<Transition> presetOrdering = new FixedOrdering<>(start, a, b, c, d);
        Comparator<Transition> postsetOrdering = new FixedOrdering<>(a, b, c, d, end);

        HashmapEncoding<Transition> presetEncoding = HashmapEncoding.ofComparableSet(presetTransitions, presetOrdering);
        HashmapEncoding<Transition> postEncoding = HashmapEncoding.ofComparableSet(postsetTransitions, postsetOrdering);

        BitEncodedSet<Transition> s1 = BitEncodedSet.empty(presetEncoding);
        s1.addAll(start, b, c);
        BitEncodedSet<Transition> s2 = BitEncodedSet.empty(postEncoding);
        BitEncodedSet<Transition> s3 = BitEncodedSet.empty(postEncoding);
        s3.addAll(c, b, end);

        BitEncodedSet<Transition>[] sets = new BitEncodedSet[]{s1, s2, s3};
        for (int i = 0; i < sets.length; i++) {
            BitEncodedSet<Transition> s = sets[i];
            System.out.println(s + " / " + s.getBitMask() + " c=" + s.cardinality() + " ,l=" + s.maxSize());
            for (int j = 0; j <= s.maxSize(); j++) {
                System.out.println("s" + i + " kMaxRange(" + j + ") = " + s.kMaxIndex(j - 1) + " - " + s.kMaxIndex(j) + " - 1 = " + s.kMaxRange(j) + " / " + s.kMaxRangeMask(j));
            }
        }

    }

    @Test
    public void tree() {

        Transition start = new Transition(Factory.UNIQUE_START_LABEL);
        Transition end = new Transition(Factory.UNIQUE_END_LABEL);
        Transition a = new Transition("a");
        Transition b = new Transition("b");
        Transition c = new Transition("c");

        Set<Transition> presetTransitions = Sets.newHashSet(start, a, b);
        Set<Transition> postsetTransitions = Sets.newHashSet(end, a, b);

        FixedOrdering<Transition> presetOrdering = new FixedOrdering<>(start, a, b, c);
        FixedOrdering<Transition> postsetOrdering = new FixedOrdering<>(a, b, c, end);

        MonotonousPlaceGenerationLogic pg = new MonotonousPlaceGenerationLogic(new IntEncodings<>(HashmapEncoding.ofComparableSet(presetTransitions, presetOrdering), HashmapEncoding.ofComparableSet(postsetTransitions, postsetOrdering)));
        EnumeratingTree<PlaceNode> tree = new EnumeratingTree<>(pg.generateRoot(), VariableExpansion.bfs());

        System.out.println(tree);

        for (int i = 0; i < 50; i++) {
            PlaceNode next = tree.tryExpandingTree();
            System.out.println("created " + i + " : " + next.getProperties());
        }

    }

    @Test
    public void bigtree() {
        int k = 10, n = 100000;
        Set<Transition> transitions = IntStream.range(0, k)
                                               .mapToObj(i -> new Transition("" + i))
                                               .collect(Collectors.toSet());

        HashmapEncoding<Transition> encoding = HashmapEncoding.ofComparableSet(transitions, Comparator.comparingInt(o -> Integer.parseInt(o.toString())));

        MonotonousPlaceGenerationLogic pg = new MonotonousPlaceGenerationLogic(new IntEncodings<>(encoding, encoding));

        EnumeratingTree<PlaceNode> tree = new EnumeratingTree<>(pg.generateRoot(), new VariableExpansion<>());


        int printerval = 5000;
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            PlaceNode expansion = tree.tryExpandingTree();
            places.add(expansion.getProperties());
            if (i % printerval == 0) {
                System.out.println(places.subList(Math.max(0, i - Math.min(i / 10, Math.min(k, 10))), i));
                System.out.println("current leaves: " + tree.getLeaves().size());
            }
        }

    }


    @Test
    public void traversal() {
        AnnotatableBiDiNodeImpl<String> root = ReflectiveNodeFactory.annotatedRoot(JavaTypingUtils.castClass(AnnotatableBiDiNodeImpl.class), "a");
        BiDiTree<AnnotatableBiDiNodeImpl<String>> tree = new BiDiTreeImpl<>(root);

        System.out.println(tree);

        AnnotatableBiDiNodeImpl<String> b = ReflectiveNodeFactory.annotatedChildOf(root, "b");
        ReflectiveNodeFactory.annotatedChildOf(root, "c");
        ReflectiveNodeFactory.annotatedChildOf(b, "d");
        AnnotatableBiDiNodeImpl<String> e = ReflectiveNodeFactory.annotatedChildOf(b, "e");
        ReflectiveNodeFactory.annotatedChildOf(e, "f");

        System.out.println(tree);
    }


    @Test
    public void ivs_math() {
        IntVectorStorage ivs1 = IntVectorStorage.zeros(new int[]{1, 1, 0, -2, 1, 2, -1, 0, 0, 1, 1, 1, 1, -1, 1}, new int[]{3, 4, 8});
        IntVectorStorage ivs2 = IntVectorStorage.zeros(new int[]{0, 1, 1, -1, 0, 1, 0, -1, 0, -2, 1, 2, 1, -1, 0}, new int[]{3, 4, 8});

        System.out.println(ivs1);
        System.out.println(ivs2);
        System.out.println(IVSComputations.interleave(ivs1, ivs2));
    }


    @Test
    public void impliciticity() {
        //new BaseDataExtractionConfig().registerDataSources();

        String[] labels = {"a", "b", "c", "d", "e"};
        Tuple2<IntEncodings<Transition>, Map<String, Transition>> tuple2 = HardcodedTestInput.setupTransitions(labels);
        Map<String, Activity> as = HardcodedTestInput.setupActivities(labels);
        IntEncodings<Transition> encs = tuple2.getT1();
        Activity a = as.get("a");
        Activity b = as.get("b");
        Activity c = as.get("c");
        Activity d = as.get("d");
        Activity e = as.get("e");
        Log log = new LogBuilderImpl().appendVariant(VariantImpl.of(a, b, c, d, e), 2)
                                      .appendVariant(VariantImpl.of(a, c, b, d, e), 1)
                                      .appendVariant(VariantImpl.of(a, c, d, b, e), 3)
                                      .build();

        Map<String, Transition> ts = tuple2.getT2();
        Transition ta = ts.get("a");
        Transition tb = ts.get("b");
        Transition tc = ts.get("c");
        Transition td = ts.get("d");
        Transition te = ts.get("e");
        PlaceMaker maker = new PlaceMaker(encs);
        Place p1 = maker.preset(ta).postset(tb).get();
        Place p2 = maker.preset(ta).postset(tc).get();
        Place p3 = maker.preset(tb).postset(te).get();
        Place p4 = maker.preset(tc).postset(td).get();
        Place p5 = maker.preset(td).postset(te).get();
        Place p6 = maker.preset(ta, tb, tc, td).postset(tb, tc, td, te).get();
        Place p7 = maker.preset(ta).postset(te).get();

        Map<Activity, Transition> mapping = HardcodedTestInput.setupMapping(as, ts);

        //new InputDataBundle(log, encs, mapping);

        MultiEncodedLog multiEncodedLog = LogEncoder.multiEncodeLog(log, encs, mapping, LogEncoder.LogEncodingParameters.getDefault());
        //MarkingHistoryBasedFitnessEvaluator fitnessEvaluator = new MarkingHistoryBasedFitnessEvaluator(multiEncodedLog, log::variantIndices, ReplayComputationParameters.getDefault(), QuickReplay::makeHistory);
        AbstractFullFitnessEvaluator ev = new MarkingHistoryBasedFitnessEvaluator.Builder().build();
        ev.globalComponentSystem().fulfilFrom(DataRequirements.CONSIDERED_VARIANTS.fulfilWith(() -> BitMask.of(0)));
        EvaluatorCollection ec = new EvaluatorCollection();
        ec.register(EvaluationRequirements.evaluator(Place.class, BasicFitnessEvaluation.class, ev::eval));

        System.out.println(ev.eval(p1));
        System.out.println(ev.eval(p2));
        System.out.println(ev.eval(p3));
        System.out.println(ev.eval(p4));
        System.out.println(ev.eval(p5));

        StatefulPlaceComposition comp = new StatefulPlaceComposition();
        comp.accept(p1);
        comp.accept(p2);
        comp.accept(p3);
        comp.accept(p4);
        comp.accept(p5);
        System.out.println(comp.rateImplicitness(p6));
        System.out.println(comp.rateImplicitness(p7));

        ev.setConsideredVariants(BitMask.of(0));
        Place pprime = maker.preset(ta, tb).postset(tc, td).get();
        StatefulPlaceComposition c2 = new StatefulPlaceComposition();
        c2.globalComponentSystem().fulfilFrom(ec);
        c2.accept(p1);
        System.out.println(c2.rateImplicitness(pprime));

        Place ptiny = maker.preset(ta).postset(td).get();
        Place psmaller = maker.preset(ta, tb).postset(tb, td).get();
        Place pbigger = maker.preset(ta, tb, tc).postset(tb, tc, td).get();
        StatefulPlaceComposition c3 = new StatefulPlaceComposition();
        c3.globalComponentSystem().fulfilFrom(ec);
        c3.accept(pbigger);
        System.out.println(c3.rateImplicitness(psmaller));

    }

    @Test
    public void indexSubsetMapping() {
        IntVectorStorage ivs1 = IntVectorStorage.zeros(new int[]{1, 1, 0, -2, 1, 2, -1, /*from*/ 0, 0, 1, 1, 1, 0, -1, /*to*/ 1}, new int[]{3, 4, 7, 1});
        IntVectorStorage ivs2 = IntVectorStorage.zeros(new int[]{1, 1, 0, -2, 1, 2, -1, /*from*/ 0, 0, 1, 1, 1, 0, -1, /*to*/ 1}, new int[]{7, 7, 1});

        IndexSubset s1 = IndexSubset.of(BitMask.of(0, 1, 2, 3));
        IndexSubset s2 = IndexSubset.of(BitMask.of(1, 2, 5));

        VariantMarkingHistories h1 = new VariantMarkingHistories(s1, ivs1);
        VariantMarkingHistories h2 = new VariantMarkingHistories(s2, ivs2);

        BitMask variantMask = BitMask.of(2);

        System.out.println("h1 gt h2: " + h1.gtOn(variantMask, h2));
        System.out.println("h1 lt h2: " + h1.ltOn(variantMask, h2));
        System.out.println("h1 ordering h2: " + VMHComputations.orderingRelationsOn(variantMask, h1, h2));
        System.out.println(h1.getAt(2));
        System.out.println(h2.getAt(2));
    }


    @Test
    public void labels() {

        Label l1 = new Label("hello.world");
        Label l2 = new Label("goodbye.world");
        RegexLabel r1 = new RegexLabel("\\w*\\.world");
        RegexLabel r2 = new RegexLabel("\\w*\\.\\w*");

        Assert.assertTrue(l1.gt(l1));
        Assert.assertTrue(l1.lt(l1));
        Assert.assertSame(l1.gt(l2), l2.lt(l1));
        Assert.assertFalse(l1.gt(l2));
        Assert.assertSame(l1.lt(l2), l2.gt(l1));
        Assert.assertFalse(l2.gt(l1));

        Assert.assertTrue(l2.gt(l2));
        Assert.assertTrue(l2.lt(l2));
        Assert.assertSame(r1.gt(r2), r2.lt(r1));
        Assert.assertTrue(r1.gt(r2));
        Assert.assertSame(r1.lt(r2), r2.gt(r1));
        Assert.assertFalse(r2.gt(r1));

        Assert.assertTrue(r1.gt(r1));
        Assert.assertTrue(r1.lt(r1));
        Assert.assertSame(r1.gt(l1), l1.lt(r1));
        Assert.assertFalse(r1.gt(l1));
        Assert.assertSame(r1.lt(l1), l1.gt(r1));
        Assert.assertTrue(l1.gt(r1));

        Assert.assertTrue(r2.gt(r2));
        Assert.assertTrue(r2.lt(r2));
        Assert.assertSame(r2.gt(l1), l1.lt(r2));
        Assert.assertFalse(r2.gt(l1));
        Assert.assertSame(r2.lt(l1), l1.gt(r2));
        Assert.assertTrue(l1.gt(r2));

        FulfilledObservableRequirement<Observation> f1 = SupervisionRequirements.observable("hello.world", Observation.class, PipeWorks.identityPipe());
        FulfilledObservableRequirement<Observation> f2 = SupervisionRequirements.observable("goodbye.world", Observation.class, PipeWorks.identityPipe());
        FulfilledObservableRequirement<Observation> f3 = SupervisionRequirements.observable("\\w*", Observation.class, PipeWorks.identityPipe());
        ObservableRequirement<Observation> req = SupervisionRequirements.observable(SupervisionRequirements.regex("\\w*.world"), Observation.class);

        Assert.assertSame(f1.gt(req), req.lt(f1.getComparable()));
        Assert.assertSame(f2.gt(req), req.lt(f2.getComparable()));
        Assert.assertSame(f3.gt(req), req.lt(f3.getComparable()));
        Assert.assertSame(f1.lt(req), req.gt(f1.getComparable()));
        Assert.assertSame(f2.lt(req), req.gt(f2.getComparable()));
        Assert.assertSame(f3.lt(req), req.gt(f3.getComparable()));

        Assert.assertTrue(f1.gt(req));
        Assert.assertTrue(f2.gt(req));
        Assert.assertFalse(f3.gt(req));
    }

    @Test
    public void transitionOrderings() {
        InputDataBundle data = SampleData.sample_1();
        Log log = data.getLog();
        BidiMap<Activity, Transition> mapping = data.getMapping();

        Map<String, Activity> activityMap = mapping.entrySet()
                                                   .stream()
                                                   .collect(Collectors.toMap(e -> e.getKey()
                                                                                   .toString(), Map.Entry::getKey));

        List<Class<? extends ActivityOrderingStrategy>> classes = new LinkedList<>();
        classes.add(AverageFirstOccurrenceIndex.class);
        classes.add(AbsoluteTraceFrequency.class);
        classes.add(AbsoluteActivityFrequency.class);
        classes.add(Lexicographic.class);


        System.out.println(activityMap.values());
        log.stream().limit(10).forEach(System.out::println);

        for (Class<? extends ActivityOrderingStrategy> aClass : classes) {
            ActivityOrderingStrategy instance = Reflection.instance(aClass, log, activityMap);
            System.out.println(aClass);
            System.out.println(instance);
            System.out.println(instance.build());
        }

    }

    @Test
    public void selfLoopMerging() {
        InputDataBundle dummyInputBundle = HardcodedTestInput.getDummyInputBundle("a", "b", "c", "d");


        NaivePlaceMaker placemaker = new NaivePlaceMaker(dummyInputBundle.getTransitionEncodings());
        Place p1 = placemaker.preset("a", "b").postset("b", "c").get();
        Place p2 = placemaker.preset("a").postset("c").get();
        Place p3 = placemaker.preset("b").postset("b", "d").get();

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
        System.out.println(p1.nonSelfLoops().setEquality(p2.nonSelfLoops()));
        System.out.println(p1.nonSelfLoops().setEquality(p3.nonSelfLoops()));
        System.out.println(p2.nonSelfLoops().setEquality(p3.nonSelfLoops()));

        SelfLoopPlaceMerger slpm = new SelfLoopPlaceMerger();

        CollectionOfPlaces pn = new CollectionOfPlaces(ImmutableSet.of(p1, p2, p3));
        System.out.println(pn);
        System.out.println(slpm.postProcess(pn));
    }

    @Test
    public void paths() throws IOException {
        OutputPathParameters aDefault = new OutputPathParameters("eSTMiner\\", "mypre_", "_mypost");
        for (PathTools.OutputFileType value : PathTools.OutputFileType.values()) {
            String filePath = aDefault.getFilePath(value, "myname");
            File file = new File(filePath);
            file.createNewFile();
        }
    }

    @Test
    public void wiring() {
        Tuple2<IntEncodings<Transition>, Map<String, Transition>> tuple2 = HardcodedTestInput.setupTransitions(Factory.UNIQUE_START_LABEL, Factory.UNIQUE_END_LABEL, "incoming", "B check", "S check", "B register", "S register", "end", "determine");
        IntEncodings<Transition> encodings = tuple2.getT1();
        Map<String, Transition> map = tuple2.getT2();
        System.out.println("encodings = " + encodings);
        UnWiringMatrix wiringMatrix = new UnWiringMatrix(encodings);

        NaivePlaceMaker pm = new NaivePlaceMaker(encodings);
        wiringMatrix.wire(pm.preset(Factory.UNIQUE_START_LABEL).postset("incoming").get());
        wiringMatrix.wire(pm.preset("incoming").postset("B check", "S check").get());
        wiringMatrix.wire(pm.preset("B check", "S check").postset("end").get());
        wiringMatrix.wire(pm.preset("end").postset(Factory.UNIQUE_END_LABEL).get());
        wiringMatrix.wire(pm.preset("B register", "S register").postset("determine").get());

        System.out.println("wiringMatrix = " + wiringMatrix);

        Place parent = pm.preset("incoming", "determine")
                         .postset("B register").get();
        BitEncodedSet<Transition> exp = BitEncodedSet.empty(encodings.post());
        exp.addAll(map.get("B register"), map.get("S register"), map.get("determine"));
        System.out.println("parent = " + parent.preset().getBitMask());
        System.out.println(exp);
        System.out.println(exp.getBitMask());
        wiringMatrix.filterPotentialSetExpansions(parent, exp.getBitMask(), MonotonousPlaceGenerationLogic.ExpansionType.Postset);
        System.out.println(exp);

        Place test = pm.preset("incoming claim", "determine likelihood of claim")
                       .postset("B register claim", "S register claim", "end").get();
        System.out.println(test);
        System.out.println(test.hashCode());
        System.out.println(wiringMatrix.isWired(test));

    }

}
