package org.processmining.specpp.config.parsing;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.componenting.data.FulfilledDataRequirement;
import org.processmining.specpp.componenting.data.ParameterRequirement;
import org.processmining.specpp.componenting.data.ParameterSourceCollection;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.link.*;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.config.*;
import org.processmining.specpp.config.components.*;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parameters.Parameters;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingStrategy;
import org.processmining.specpp.supervision.Supervisor;
import org.processmining.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.specpp.supervision.supervisors.TerminalSupervisor;
import org.processmining.specpp.util.JavaTypingUtils;
import org.processmining.specpp.util.Reflection;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ConfigurationParsing {

    public static final TypeAdapter<InputProcessingConfig> INPUT_PROCESSING_CONFIG_TYPE_ADAPTER = new TypeAdapter<InputProcessingConfig>() {

        private final Gson gson = new Gson();

        @Override
        public void write(JsonWriter out, InputProcessingConfig value) throws IOException {
            out.nullValue();
        }

        @Override
        public InputProcessingConfig read(JsonReader in) throws IOException {
            PreProcessingParameters ppp = PreProcessingParameters.getDefault();
            DataExtractionParameters dep = DataExtractionParameters.getDefault();
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                JsonElement jsonElement = gson.fromJson(in, JsonElement.class);
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                XEventClassifier eventClassifier = ppp.getEventClassifier();
                if (jsonObject.has("eventClassifier")) try {
                    String eventClassifierString = jsonObject.get("eventClassifier").getAsString();
                    Class<XEventClassifier> eventClassifierClass = (Class<XEventClassifier>) Class.forName(getFullyQualifiedClassName(BasePackage.XES_Classifier, eventClassifierString));
                    eventClassifier = Reflection.instance(eventClassifierClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                boolean addStartEndTransitions = ppp.isAddStartEndTransitions();
                if (jsonObject.has("addStartEndTransitions"))
                    addStartEndTransitions = jsonObject.get("addStartEndTransitions").getAsBoolean();

                ppp = new PreProcessingParameters(eventClassifier, addStartEndTransitions);

                Class<? extends ActivityOrderingStrategy> activityOrderingStrategyClass = dep.getActivityOrderingStrategy();

                if (jsonObject.has("activityOrderingStrategy")) try {
                    String activityOrderingStrategyString = jsonObject.get("activityOrderingStrategy").getAsString();
                    activityOrderingStrategyClass = (Class<? extends ActivityOrderingStrategy>) Class.forName(getFullyQualifiedClassName(BasePackage.ActivityOrdering, activityOrderingStrategyString));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                dep = new DataExtractionParameters(activityOrderingStrategyClass);

            }
            return new InputProcessingConfigImpl(ppp, dep);
        }
    };

    public static final TypeAdapter<SupervisionConfiguration.Configurator> SUPERVISORS_TYPE_ADAPTER = new TypeAdapter<SupervisionConfiguration.Configurator>() {

        @Override
        public void write(JsonWriter out, SupervisionConfiguration.Configurator value) throws IOException {
            out.nullValue();
        }

        @Override
        public SupervisionConfiguration.Configurator read(JsonReader in) throws IOException {
            SupervisionConfiguration.Configurator sc = Configurators.supervisors();
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginArray();
                boolean nonEmpty = in.hasNext();
                if (nonEmpty) sc.addSupervisor(BaseSupervisor::new);
                while (in.hasNext()) {
                    String s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Supervisors, s);
                    SimpleBuilder<? extends Supervisor> simpleBuilder = ConfigurationParsing.parseBuilder(s, Supervisor.class);
                    sc.addSupervisor(simpleBuilder);
                }
                in.endArray();
                if (nonEmpty) sc.addSupervisor(TerminalSupervisor::new);
            }
            return sc;
        }
    };

    public static final TypeAdapter<EvaluatorConfiguration.Configurator> EVALUATORS_TYPE_ADAPTER = new TypeAdapter<EvaluatorConfiguration.Configurator>() {
        @Override
        public void write(JsonWriter out, EvaluatorConfiguration.Configurator value) throws IOException {
            out.nullValue();
        }

        @Override
        public EvaluatorConfiguration.Configurator read(JsonReader in) throws IOException {
            EvaluatorConfiguration.Configurator ec = Configurators.evaluators();
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginArray();
                while (in.hasNext()) {
                    String s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Evaluation, s);
                    SimpleBuilder<? extends ProvidesEvaluators> simpleBuilder = ConfigurationParsing.parseBuilder(s, ProvidesEvaluators.class);
                    ec.addEvaluatorProvider(simpleBuilder);
                }
                in.endArray();
            }
            return ec;
        }
    };

    public static final TypeAdapter<EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode>> EFFICIENT_TREE_TYPE_ADAPTER = new TypeAdapter<EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode>>() {
        @Override
        public void write(JsonWriter out, EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> value) throws IOException {
            out.nullValue();
        }

        @Override
        public EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> read(JsonReader in) throws IOException {
            EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etc = Configurators.generatingTree();
            HeuristicTreeConfiguration.Configurator<Place, PlaceState, PlaceNode, TreeNodeScore> htc = Configurators.heuristicTree();
            boolean isHeuristic = false;
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginObject();

                String name;
                name = in.nextName();
                assert "tree".equalsIgnoreCase(name);
                String s = in.nextString();
                s = getFullyQualifiedClassName(BasePackage.Tree, s);
                InitializingBuilder<? extends EfficientTreeComponent<PlaceNode>, ExpansionStrategyComponent<PlaceNode>> tree = ConfigurationParsing.parseInitializingBuilder(s, JavaTypingUtils.castClass(EfficientTreeComponent.class), JavaTypingUtils.castClass(ExpansionStrategyComponent.class));
                etc.tree(tree);
                htc.tree(tree);

                name = in.nextName();
                assert "expansion strategy".equalsIgnoreCase(name);
                s = in.nextString();
                s = getFullyQualifiedClassName(BasePackage.TreeExpansion, s);
                String expansionStrategyClass = s;
                SimpleBuilder<? extends ExpansionStrategyComponent<PlaceNode>> exp = parseBuilder(s, JavaTypingUtils.castClass(ExpansionStrategyComponent.class));
                etc.expansionStrategy(exp);

                name = in.nextName();
                assert "node generation logic".equalsIgnoreCase(name);
                s = in.nextString();
                s = getFullyQualifiedClassName(BasePackage.NodeGeneration, s);
                SimpleBuilder<? extends ChildGenerationLogicComponent<Place, PlaceState, PlaceNode>> gen = parseBuilder(s, JavaTypingUtils.castClass(ChildGenerationLogicComponent.class));
                etc.childGenerationLogic(gen);
                htc.childGenerationLogic(gen);

                if (in.hasNext() && "heuristic".equalsIgnoreCase(in.nextName())) {
                    s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Heuristic, s);
                    SimpleBuilder<? extends HeuristicStrategy<PlaceNode, TreeNodeScore>> heu = parseBuilder(s, JavaTypingUtils.castClass(HeuristicStrategy.class));
                    InitializingBuilder<? extends HeuristicTreeExpansion<PlaceNode, TreeNodeScore>, HeuristicStrategy<PlaceNode, TreeNodeScore>> heu_exp = parseInitializingBuilder(expansionStrategyClass, JavaTypingUtils.castClass(HeuristicTreeExpansion.class), JavaTypingUtils.castClass(HeuristicStrategy.class));
                    htc.heuristic(heu);
                    htc.heuristicExpansion(heu_exp);
                    isHeuristic = true;
                }

                in.endObject();
            }
            return isHeuristic ? htc : etc;
        }
    };

    public static final TypeAdapter<PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper>> POST_PROCESSING_TYPE_ADAPTER = new TypeAdapter<PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper>>() {
        @Override
        public void write(JsonWriter out, PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> value) throws IOException {
            out.nullValue();
        }

        @Override
        public PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> read(JsonReader in) throws IOException {
            PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> configurator = Configurators.<CollectionOfPlaces>postProcessing()
                                                                                                                          .addPostProcessor(ProMConverter::new);
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginArray();
                PostProcessingConfiguration.Configurator ppc = Configurators.postProcessing();
                while (in.hasNext()) {
                    String s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.PostProcessor, s);
                    SimpleBuilder<? extends PostProcessor> simpleBuilder = parseBuilder(s, PostProcessor.class);
                    ppc = ppc.addPostProcessor(simpleBuilder);
                }
                in.endArray();
                // ppc.addPostProcessor(ProMConverter::new);
                configurator = (PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper>) ppc;
            }
            return configurator;
        }
    };


    public static final TypeAdapter<FulfilledDataRequirement<? extends Parameters>> PARAMETER_REQUIREMENT_TYPE_ADAPTER = new TypeAdapter<FulfilledDataRequirement<? extends Parameters>>() {

        private final Gson gson = new Gson();

        @Override
        public void write(JsonWriter out, FulfilledDataRequirement<? extends Parameters> value) throws IOException {
            out.nullValue();
        }

        @Override
        public FulfilledDataRequirement<? extends Parameters> read(JsonReader in) throws IOException {
            FulfilledDataRequirement<? extends Parameters> freq = null;
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginObject();
                String s = in.nextName();
                assert "label".equalsIgnoreCase(s);
                String key = in.nextString();
                s = in.nextName();
                assert "type".equalsIgnoreCase(s);
                String type = in.nextString();
                type = getFullyQualifiedClassName(BasePackage.Parameters, type);
                Class<Parameters> forName;
                try {
                    forName = (Class<Parameters>) Class.forName(type);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                Class<Parameters> aClass = forName;
                s = in.nextName();
                assert "args".equalsIgnoreCase(s);
                Parameters o = aClass.cast(gson.fromJson(in, aClass));
                ParameterRequirement<Parameters> req = new ParameterRequirement<>(key, aClass);
                freq = req.fulfilWithStatic(o);
                in.endObject();
            }
            return freq;
        }
    };

    public static final TypeAdapter<ProvidesParameters> PARAMETERS_TYPE_ADAPTER = new TypeAdapter<ProvidesParameters>() {
        @Override
        public void write(JsonWriter out, ProvidesParameters value) throws IOException {
            out.nullValue();
        }

        @Override
        public ProvidesParameters read(JsonReader in) throws IOException {
            ProvidesParameters pp = new ParameterProvider() {
                @Override
                public void init() {
                }
            };
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                if (in.peek() == JsonToken.BEGIN_OBJECT) {
                    in.beginObject();
                    String name = in.nextName();
                    assert "base".equalsIgnoreCase(name);
                    String s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Configs, s);
                    ProvidesParameters base_pp = parseBuilder(s, ProvidesParameters.class).get();

                    name = in.nextName();
                    assert "extensions".equalsIgnoreCase(name);
                    List<FulfilledDataRequirement<? extends Parameters>> list = readParameterList(in);
                    ParameterSourceCollection psc = new ParameterSourceCollection();
                    for (FulfilledDataRequirement<? extends Parameters> f : list) {
                        psc.register(f);
                    }
                    base_pp.globalComponentSystem().overridingAbsorb(psc);
                    pp = base_pp;
                    in.endObject();
                } else if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    List<FulfilledDataRequirement<? extends Parameters>> list = readParameterList(in);

                    pp = new ParameterProvider() {
                        @Override
                        public void init() {
                            for (FulfilledDataRequirement<? extends Parameters> f : list) {
                                globalComponentSystem().provide(f);
                            }
                        }
                    };
                }

            }
            return pp;
        }

        private List<FulfilledDataRequirement<? extends Parameters>> readParameterList(JsonReader in) throws IOException {
            List<FulfilledDataRequirement<? extends Parameters>> list = new LinkedList<>();
            in.beginArray();
            while (in.hasNext()) {
                FulfilledDataRequirement<? extends Parameters> freq = PARAMETER_REQUIREMENT_TYPE_ADAPTER.read(in);
                list.add(freq);
            }
            in.endArray();
            return list;
        }
    };

    public static final TypeAdapter<ComponentConfig> CONFIGURATOR_COLLECTION_TYPE_ADAPTER = new TypeAdapter<ComponentConfig>() {
        @Override
        public void write(JsonWriter out, ComponentConfig value) throws IOException {
            out.nullValue();
        }

        @Override
        public ComponentConfig read(JsonReader in) throws IOException {
            ComponentConfigImpl cc = null;
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginObject(); // components

                // ** Supervisors ** //
                String name = in.nextName();
                assert "supervisors".equalsIgnoreCase(name);
                SupervisionConfiguration.Configurator sc = SUPERVISORS_TYPE_ADAPTER.read(in);

                // ** Evaluators ** //
                name = in.nextName();
                assert "evaluators".equalsIgnoreCase(name);
                EvaluatorConfiguration.Configurator evc = EVALUATORS_TYPE_ADAPTER.read(in);

                // ** Proposing ** //
                name = in.nextName();
                assert "proposing".equals(name);
                in.beginObject(); // proposing
                ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, CollectionOfPlaces> pcc = Configurators.proposerComposer();


                name = in.nextName();
                assert "proposer".equalsIgnoreCase(name);
                String s = in.nextString();
                s = getFullyQualifiedClassName(BasePackage.Proposal, s);
                SimpleBuilder<? extends ProposerComponent<Place>> prop = parseBuilder(s, JavaTypingUtils.castClass(ProposerComponent.class));
                pcc.proposer(prop);
                name = in.nextName();
                assert "tree structure".equalsIgnoreCase(name);
                EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etc = EFFICIENT_TREE_TYPE_ADAPTER.read(in);
                in.endObject(); // proposing

                // ** Compositing ** //
                name = in.nextName();
                assert "compositing".equalsIgnoreCase(name);
                in.beginObject(); // compositing

                name = in.nextName();
                assert "composition".equalsIgnoreCase(name);
                if (in.peek() == JsonToken.STRING) {
                    s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Composition, s);
                    SimpleBuilder<? extends AdvancedComposition<Place>> co = parseBuilder(s, JavaTypingUtils.castClass(AdvancedComposition.class));
                    pcc.composition(co);
                } else if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    in.beginArray();

                    SimpleBuilder<? extends AdvancedComposition<Place>> term = null;
                    List<InitializingBuilder<? extends AdvancedComposition<Place>, ? super AdvancedComposition<Place>>> l = new LinkedList<>();

                    while (in.hasNext()) {
                        s = in.nextString();
                        s = getFullyQualifiedClassName(BasePackage.Composition, s);
                        if (in.hasNext()) {
                            InitializingBuilder<? extends AdvancedComposition<Place>, ? super AdvancedComposition<Place>> rec = parseInitializingBuilder(s, JavaTypingUtils.castClass(AdvancedComposition.class), JavaTypingUtils.castClass(AdvancedComposition.class));
                            l.add(rec);
                        } else {
                            term = parseBuilder(s, JavaTypingUtils.castClass(AdvancedComposition.class));
                        }
                    }
                    in.endArray();
                    InitializingBuilder[] arr = l.toArray(new InitializingBuilder[0]);
                    pcc.terminalComposition(term).recursiveCompositions(arr);
                }

                name = in.nextName();
                assert "composer".equalsIgnoreCase(name);
                if (in.peek() == JsonToken.STRING) {
                    s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Composer, s);
                    InitializingBuilder<? extends ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>, Object> tc = parseInitializingBuilder(s, JavaTypingUtils.castClass(ComposerComponent.class), JavaTypingUtils.castClass(CompositionComponent.class));
                    pcc.composer(tc);
                } else if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    in.beginArray();
                    InitializingBuilder<? extends ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>, AdvancedComposition<Place>> termcomp = null;
                    List<InitializingBuilder<? extends ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>, ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>>> l = new LinkedList<>();
                    while (in.hasNext()) {
                        s = in.nextString();
                        s = getFullyQualifiedClassName(BasePackage.Composer, s);
                        if (in.hasNext()) {
                            InitializingBuilder<? extends ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>, ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>> compcomp = parseInitializingBuilder(s, JavaTypingUtils.castClass(ComposerComponent.class), JavaTypingUtils.castClass(ComposerComponent.class));
                            l.add(compcomp);
                        } else {
                            termcomp = parseInitializingBuilder(s, JavaTypingUtils.castClass(ComposerComponent.class), JavaTypingUtils.castClass(AdvancedComposition.class));
                        }
                    }
                    in.endArray();
                    InitializingBuilder[] arr = l.toArray(new InitializingBuilder[0]);
                    pcc.terminalComposer(termcomp).recursiveComposers(arr);
                }
                in.endObject(); // compositing

                // ** Post Processing ** //
                name = in.nextName();
                assert "post processors".equalsIgnoreCase(name);
                PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppc = POST_PROCESSING_TYPE_ADAPTER.read(in);

                in.endObject(); // components

                cc = new ComponentConfigImpl(sc, pcc, evc, etc, ppc);
            }
            return cc;
        }
    };

    public static final TypeAdapter<SPECppConfigBundle> PARSED_CONFIG_TYPE_ADAPTER = new TypeAdapter<SPECppConfigBundle>() {
        @Override
        public void write(JsonWriter out, SPECppConfigBundle value) throws IOException {
            out.nullValue();
        }

        @Override
        public SPECppConfigBundle read(JsonReader in) throws IOException {
            InputProcessingConfig idc = null;
            ComponentConfig cc = null;
            ProvidesParameters pp = null;

            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginObject(); // root

                String name;
                name = in.nextName();
                if ("input processing".equalsIgnoreCase(name)) {
                    idc = INPUT_PROCESSING_CONFIG_TYPE_ADAPTER.read(in);
                    name = in.nextName();
                }

                assert "components".equalsIgnoreCase(name);
                cc = CONFIGURATOR_COLLECTION_TYPE_ADAPTER.read(in);

                // ** Parameters (optional) ** //
                if (in.hasNext()) {
                    name = in.nextName();
                    assert "parameters".equalsIgnoreCase(name);
                    pp = PARAMETERS_TYPE_ADAPTER.read(in);
                }

                in.endObject(); // root

            }
            return ConfigFactory.create(idc, cc, ConfigFactory.create(pp));
        }
    };

    public static TypeAdapter<SPECppConfigBundle> getTypeAdapter() {
        return PARSED_CONFIG_TYPE_ADAPTER;
    }


    public enum BasePackage {
        Evaluation("org.processmining.specpp.evaluation"),
        Supervisors("org.processmining.specpp.supervision.supervisors"),
        TreeExpansion("org.processmining.specpp.datastructures.tree"),
        NodeGeneration("org.processmining.specpp.datastructures.tree.nodegen"),
        Heuristic("org.processmining.specpp.evaluation.heuristics"),
        Composer("org.processmining.specpp.composition.composers"),
        Composition("org.processmining.specpp.composition"),
        Proposal("org.processmining.specpp.proposal"),
        PostProcessor("org.processmining.specpp.postprocessing"),
        Parameters("org.processmining.specpp.config.parameters"),
        XES_Classifier("org.deckfour.xes.classification"),
        ActivityOrdering("org.processmining.specpp.preprocessing.orderings"),
        Configs("org.processmining.specpp.config.presets"),
        Tree("org.processmining.specpp.datastructures.tree.base.impls");

        private final String packagePath;

        BasePackage(String packagePath) {
            this.packagePath = packagePath;
        }

        public String getPackagePath() {
            return packagePath;
        }

    }

    public static String getFullyQualifiedClassName(BasePackage basePackage, String s) {
        // if the string starts like that, it is already fully qualified, otherwise, we guess with the 'typical' base package as a prefix
        if (s.startsWith("org.processmining")) return s;
        else return basePackage.getPackagePath() + "." + s;
    }

    public static <I> SimpleBuilder<? extends I> parseBuilder(String s, Class<I> typeToParse) {
        SimpleBuilder<? extends I> simpleBuilder = null;
        try {
            Class<?> aClass = Class.forName(s);
            if (typeToParse.isAssignableFrom(aClass)) {
                Class<? extends I> directClassType = aClass.asSubclass(typeToParse);
                simpleBuilder = () -> Reflection.instance(directClassType);
            } else if (SimpleBuilder.class.isAssignableFrom(aClass)) {
                Class<? extends SimpleBuilder<? extends I>> builderClassType = aClass.asSubclass(JavaTypingUtils.castClass(SimpleBuilder.class));
                simpleBuilder = Reflection.instance(builderClassType);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return simpleBuilder;
    }

    private static <T, A> InitializingBuilder<? extends T, A> parseInitializingBuilder(String s, Class<T> typeToParse, Class<A> argType) {
        InitializingBuilder<? extends T, A> initializingBuilder = null;
        try {
            Class<?> aClass = Class.forName(s);
            if (typeToParse.isAssignableFrom(aClass)) {
                Class<? extends T> directClassType = aClass.asSubclass(typeToParse);
                initializingBuilder = a -> Reflection.instance(directClassType, a);
            } else if (InitializingBuilder.class.isAssignableFrom(aClass)) {
                Class<? extends InitializingBuilder<? extends T, A>> builderClassType = aClass.asSubclass(JavaTypingUtils.castClass(InitializingBuilder.class));
                initializingBuilder = Reflection.instance(builderClassType);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return initializingBuilder;
    }

    public static Gson createGson() {
        return new GsonBuilder().registerTypeAdapter(SPECppConfigBundle.class, PARSED_CONFIG_TYPE_ADAPTER).create();
    }

}