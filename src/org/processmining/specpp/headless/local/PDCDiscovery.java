package org.processmining.specpp.headless.local;

import org.apache.commons.lang.StringUtils;
import org.processmining.specpp.headless.ProMLessSPECpp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PDCDiscovery {


    public static final String FOLDER_PATH = "C:\\Users\\Leah\\Desktop\\PDC2022_Attempt\\";

    public static void main(String[] args) {

        int[] choices = new int[]{2, 3, 2, 2, 2, 2, 5};

        int max = 0b111111;
        List<String> strings = new ArrayList<>();
        for (String s : binary(max)) {
            strings.add(s);
            if (s.charAt(1) == '1') strings.add(s.charAt(0) + "2" + s.substring(2));
        }

        strings.stream().forEachOrdered(s -> {
            for (int noise = 0; noise < 1; noise++) {
                String logIdentifier = s + noise;
                System.out.println("Starting on " + logIdentifier);
                discover(logIdentifier);
                System.out.println("Finished with " + logIdentifier);
            }
        });

    }

    public static List<String> binary(int max) {
        ArrayList<String> result = new ArrayList<>();
        String s = Integer.toBinaryString(max);
        int highestOneBit = s.length();
        int i = 0;
        do {
            result.add(StringUtils.leftPad(Integer.toBinaryString(i), highestOneBit, '0'));
            i++;
        } while ((i & max) != max);
        result.add(s);
        return result;
    }

    public static List<int[]> succ_combinations(int[] arr, int i, int[] choices) {
        ArrayList<int[]> results = new ArrayList<>();

        if (i >= choices.length) return results;
        int[] ints = Arrays.copyOf(arr, arr.length);
        for (int n = 0; n < choices[i]; n++) {
            ints[i] = n;
            for (int j = i + 1; j < choices.length; j++) {
                for (int l = 0; l < choices[j]; l++) {
                    int[] e = Arrays.copyOf(ints, ints.length);
                    e[j] = l;
                    for (int k = 0; j + k < choices.length; k++) {
                        for (int m = 0; m < choices[j + k]; m++) {
                            int[] f = Arrays.copyOf(e, ints.length);
                            f[j + k] = m;
                            results.add(f);
                        }
                    }
                }
            }
            //results.addAll(succ_combinations(ints, i + 1, choices));
        }
        return results;
    }

    public static void discover(String logIdentifier) {
        String fullLogName = String.format("pdc2022_%s", logIdentifier);
        String trainingPath = FOLDER_PATH + "Training_Logs\\" + fullLogName + ".xes";
        String petrinetPath = FOLDER_PATH + "Discovered_Models\\" + fullLogName + ".pnml";
        ProMLessSPECpp.run(trainingPath, petrinetPath);
    }

}
