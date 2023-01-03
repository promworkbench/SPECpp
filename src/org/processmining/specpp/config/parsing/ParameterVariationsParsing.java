package org.processmining.specpp.config.parsing;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.processmining.specpp.componenting.data.FulfilledDataRequirement;
import org.processmining.specpp.componenting.data.ParameterRequirement;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parameters.Parameters;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.processmining.specpp.config.parsing.ConfigurationParsing.getFullyQualifiedClassName;

public class ParameterVariationsParsing {

    public static final TypeAdapter<List<ProvidesParameters>> PARAMETER_PROVIDER_LIST_TYPE_ADAPTER = new TypeAdapter<List<ProvidesParameters>>() {

        private final Gson gson = new Gson();

        @Override
        public void write(JsonWriter out, List<ProvidesParameters> value) throws IOException {
            out.nullValue();
        }

        @Override
        public List<ProvidesParameters> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return Lists.newArrayList();
            } else {
                List<List<Tuple2<ParameterRequirement<Parameters>, List<Parameters>>>> listOfListsOfLists = new ArrayList<>();
                JsonArray jsonArray = gson.fromJson(in, JsonArray.class);
                for (JsonElement next : jsonArray) {
                    if (next.isJsonArray()) {
                        // param group which are varied together, same length required

                        List<Tuple2<ParameterRequirement<Parameters>, List<Parameters>>> blocks = new ArrayList<>();
                        for (JsonElement jsonBlock : next.getAsJsonArray()) {
                            blocks.add(readParameterBlock(jsonBlock));
                        }

                        assert blocks.stream().mapToInt(t -> t.getT2().size()).distinct().count() == 1;
                        listOfListsOfLists.add(blocks);
                    } else if (next.isJsonObject()) {
                        List<Tuple2<ParameterRequirement<Parameters>, List<Parameters>>> blocks = new ArrayList<>();
                        blocks.add(readParameterBlock(next));
                        listOfListsOfLists.add(blocks);
                    }
                }

                //

                int V = listOfListsOfLists.size();
                List<Integer> innerVariations = listOfListsOfLists.stream()
                                                                  .map(l -> l.get(0).getT2().size())
                                                                  .collect(Collectors.toList());
                int[] temp = innerVariations.stream().mapToInt(i -> i).toArray();
                Arrays.parallelPrefix(temp, (a, b) -> a * b);
                int totalVariationCount = temp[V - 1];
                int[] cumulativeVariations = new int[V];
                cumulativeVariations[0] = 1;
                System.arraycopy(temp, 0, cumulativeVariations, 1, V - 1);

                List<ProvidesParameters> variations = new ArrayList<>(totalVariationCount);
                for (int i = 0; i < totalVariationCount; i++) {
                    List<FulfilledDataRequirement<Parameters>> variation = new ArrayList<>();
                    for (int j = 0; j < V; j++) {
                        int l = innerVariations.get(j);
                        int k = (i / cumulativeVariations[j]) % l;
                        List<Tuple2<ParameterRequirement<Parameters>, List<Parameters>>> tuple2s = listOfListsOfLists.get(j);
                        List<FulfilledDataRequirement<Parameters>> collect = tuple2s.stream()
                                                                                    .map(t -> t.getT1()
                                                                                               .fulfilWithStatic(t.getT2()
                                                                                                                  .get(k)))
                                                                                    .collect(Collectors.toList());
                        variation.addAll(collect);
                    }
                    ParameterProvider pp = new ParameterProvider() {
                        @Override
                        public void init() {
                            for (FulfilledDataRequirement<? extends Parameters> f : variation) {
                                globalComponentSystem().provide(f);
                            }
                        }
                    };
                    variations.add(pp);
                }

                return variations;
            }
        }

        private Tuple2<ParameterRequirement<Parameters>, List<Parameters>> readParameterBlock(JsonElement next) {
            JsonObject parameterBlock = next.getAsJsonObject();
            String label = parameterBlock.get("label").getAsString();
            String type = parameterBlock.get("type").getAsString();
            boolean vary_args_independently = parameterBlock.has("vary args independently") && parameterBlock.get("vary args independently")
                                                                                                             .getAsBoolean();
            type = getFullyQualifiedClassName(ConfigurationParsing.BasePackage.Parameters, type);
            Class<Parameters> forName;
            try {
                forName = (Class<Parameters>) Class.forName(type);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Class<Parameters> parameterClass = forName;
            JsonObject args = parameterBlock.get("args").getAsJsonObject();
            List<String> argNames = args.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
            TypeAdapter<Parameters> adapter = gson.getAdapter(parameterClass);

            List<Parameters> parameterList;
            if (vary_args_independently) {
                List<List<JsonElement>> listOfArgValueLists = new ArrayList<>();
                for (int i = 0; i < argNames.size(); i++) {
                    String currentArg = argNames.get(i);
                    JsonElement argElement = args.get(currentArg);
                    if (argElement.isJsonArray()) {
                        listOfArgValueLists.add(Lists.newArrayList(argElement.getAsJsonArray().iterator()));
                    } else {
                        ArrayList<JsonElement> e = Lists.newArrayList(argElement);
                        assert e.size() == 1;
                        listOfArgValueLists.add(e);
                    }
                }

                int V = listOfArgValueLists.size();
                int totalVariations = listOfArgValueLists.stream().mapToInt(List::size).reduce(1, (a, b) -> a * b);
                int[] temp = listOfArgValueLists.stream().mapToInt(List::size).toArray();
                Arrays.parallelPrefix(temp, (a, b) -> a * b);
                int[] cumulativeVariations = new int[V];
                cumulativeVariations[0] = 1;
                System.arraycopy(temp, 0, cumulativeVariations, 1, V - 1);

                parameterList = new ArrayList<>(totalVariations);
                for (int i = 0; i < totalVariations; i++) {
                    JsonObject o = new JsonObject();
                    for (int j = 0; j < V; j++) {
                        String s = argNames.get(j);
                        List<JsonElement> list = listOfArgValueLists.get(j);
                        int k = (i / cumulativeVariations[j]) % list.size();
                        JsonElement jsonElement = list.get(k);
                        o.add(s, jsonElement);
                    }
                    parameterList.add(adapter.fromJsonTree(o));
                }

            } else {
                assert argNames.stream().mapToInt(a -> args.get(a).getAsJsonArray().size()).distinct().count() == 1;
                int count = args.get(argNames.get(0)).getAsJsonArray().size();
                parameterList = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    JsonObject o = new JsonObject();
                    for (String s : argNames) {
                        JsonElement jsonElement = args.get(s).getAsJsonArray().get(i);
                        o.add(s, jsonElement);
                    }
                    Parameters paramInstance = adapter.fromJsonTree(o);
                    parameterList.add(paramInstance);
                }
            }
            return new ImmutableTuple2<>(new ParameterRequirement<>(label, parameterClass), parameterList);
        }
    };

    public static TypeAdapter<List<ProvidesParameters>> getTypeAdapter() {
        return PARAMETER_PROVIDER_LIST_TYPE_ADAPTER;
    }

}
