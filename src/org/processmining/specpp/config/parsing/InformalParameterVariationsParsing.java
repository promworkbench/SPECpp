package org.processmining.specpp.config.parsing;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.util.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InformalParameterVariationsParsing {

    public static void main(String[] args) {
        List<Tuple2<String, List<String>>> list = FileUtils.readCustomJson("input/eval_configs/variations.json", getTypeAdapter());
        FileUtils.saveAsCSV("test.csv", list);
    }

    public static final TypeAdapter<List<Tuple2<String, List<String>>>> PARAMETER_PROVIDER_LIST_TYPE_ADAPTER = new TypeAdapter<List<Tuple2<String, List<String>>>>() {

        private final Gson gson = new Gson();

        @Override
        public void write(JsonWriter out, List<Tuple2<String, List<String>>> value) throws IOException {
            out.nullValue();
        }

        @Override
        public List<Tuple2<String, List<String>>> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return Lists.newArrayList();
            } else {
                List<List<List<Tuple2<String, List<String>>>>> listOfListOfListOfPairs = new ArrayList<>();
                JsonArray jsonArray = gson.fromJson(in, JsonArray.class);
                for (JsonElement next : jsonArray) {
                    List<List<Tuple2<String, List<String>>>> blocks = new ArrayList<>();
                    if (next.isJsonArray()) {
                        // param group which are varied together, same length required
                        for (JsonElement jsonBlock : next.getAsJsonArray()) {
                            blocks.add(readParameterBlock(jsonBlock));
                        }
                    } else if (next.isJsonObject()) {
                        blocks.add(readParameterBlock(next));
                    }
                    assert blocks.stream().mapToInt(List::size).distinct().count() == 1;
                    listOfListOfListOfPairs.add(blocks);
                }

                //

                int V = listOfListOfListOfPairs.size();
                List<Integer> innerVariations = listOfListOfListOfPairs.stream()
                                                                       .map(l -> l.get(0).get(0).getT2().size())
                                                                       .collect(Collectors.toList());
                int[] temp = innerVariations.stream().mapToInt(i -> i).toArray();
                Arrays.parallelPrefix(temp, (a, b) -> a * b);
                int totalVariationCount = temp[V - 1];
                int[] cumulativeVariations = new int[V];
                cumulativeVariations[0] = 1;
                System.arraycopy(temp, 0, cumulativeVariations, 1, V - 1);


                List<Tuple2<String, List<String>>> variations = new ArrayList<>();
                listOfListOfListOfPairs.stream()
                                       .flatMap(ll -> ll.stream().flatMap(l -> l.stream().map(Tuple2::getT1)))
                                       .forEachOrdered(s -> variations.add(new ImmutableTuple2<>(s, new ArrayList<>())));

                for (int i = 0; i < totalVariationCount; i++) {
                    int x = 0;
                    for (int j = 0; j < V; j++) {
                        int l = innerVariations.get(j);
                        int k = (i / cumulativeVariations[j]) % l;
                        for (List<Tuple2<String, List<String>>> list : listOfListOfListOfPairs.get(j)) {
                            for (Tuple2<String, List<String>> tup : list) {
                                variations.get(x).getT2().add(tup.getT2().get(k));
                                x++;
                            }
                        }
                    }

                }

                return variations;
            }
        }

        private List<Tuple2<String, List<String>>> readParameterBlock(JsonElement next) {
            JsonObject parameterBlock = next.getAsJsonObject();
            boolean vary_args_independently = parameterBlock.has("vary args independently") && parameterBlock.get("vary args independently")
                                                                                                             .getAsBoolean();
            JsonObject args = parameterBlock.get("args").getAsJsonObject();
            List<String> argNames = args.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());

            List<Tuple2<String, List<String>>> parameterList = new ArrayList<>();
            for (String s : argNames) {
                parameterList.add(new ImmutableTuple2<>(s, new ArrayList<>()));
            }

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

                for (int i = 0; i < totalVariations; i++) {
                    for (int j = 0; j < V; j++) {
                        List<JsonElement> list = listOfArgValueLists.get(j);
                        int k = (i / cumulativeVariations[j]) % list.size();
                        parameterList.get(j).getT2().add(list.get(k).getAsString());
                    }
                }

            } else {
                int maxLength = argNames.stream()
                                        .map(args::get)
                                        .filter(JsonElement::isJsonArray)
                                        .map(JsonElement::getAsJsonArray)
                                        .mapToInt(JsonArray::size)
                                        .max()
                                        .orElse(1);

                assert argNames.stream()
                               .map(args::get)
                               .filter(JsonElement::isJsonArray)
                               .map(JsonElement::getAsJsonArray)
                               .mapToInt(JsonArray::size)
                               .allMatch(i -> i == maxLength);

                for (int i = 0; i < maxLength; i++) {
                    for (int j = 0; j < argNames.size(); j++) {
                        String s = argNames.get(j);
                        JsonElement entry = args.get(s);
                        JsonElement jsonElement = entry.isJsonArray() ? entry.getAsJsonArray().get(i) : entry;
                        parameterList.get(j).getT2().add(jsonElement.getAsString());
                    }
                }
            }

            return parameterList;
        }
    };

    public static TypeAdapter<List<Tuple2<String, List<String>>>> getTypeAdapter() {
        return PARAMETER_PROVIDER_LIST_TYPE_ADAPTER;
    }


}
