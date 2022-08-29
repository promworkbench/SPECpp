package org.processmining.specpp.orchestra;

import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.base.impls.SPECppBuilder;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.composition.PlaceCollection;
import org.processmining.specpp.config.Configuration;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.supervision.traits.ProvidesOngoingVisualization;
import org.processmining.specpp.util.FileUtils;
import org.processmining.specpp.util.PathTools;
import org.processmining.specpp.util.VizUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SPECppOperations {

    public static List<SPECpp<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper>> configureAndExecuteMultiple(DataSource<InputDataBundle> inputDataBundleSource, List<ProvidesParameters> parametersList, boolean doInParallel) {
        Stream<ProvidesParameters> stream = parametersList.stream();
        if (doInParallel) stream = stream.parallel();
        LocalDateTime start = LocalDateTime.now();
        System.out.println("# Commencing " + parametersList.size() + " Multi SpecOps" + (doInParallel ? " in parallel" : "") + " @" + start);
        System.out.println("// ========================================= //");

        List<SPECpp<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper>> collect = stream.map(pp -> SPECppOperations.configureAndExecute(() -> new CustomSPECppConfigBundle(pp), inputDataBundleSource, false, true, true))
                                                                                            .collect(Collectors.toList());
        System.out.println("// ========================================= //");
        LocalDateTime end = LocalDateTime.now();
        System.out.println("# Finished Multi SpecOps in " + Duration.between(start, end)
                                                                    .toString()
                                                                    .substring(2) + " @" + end);

        String s = collect.stream()
                          .map(SPECpp::getGlobalComponentRepository)
                          .map(GlobalComponentRepository::parameters)
                          .map(dc -> dc.askForData(ParameterRequirements.OUTPUT_PATH_PARAMETERS))
                          .map(opp -> opp.getFolderPath(PathTools.FolderStructure.BASE_OUTPUT_FOLDER))
                          .distinct()
                          .findFirst()
                          .orElse("");
        System.out.println("Outputs were saved to " + s);

        return collect;
    }


    public static SPECpp<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> configureAndExecute(DataSource<SPECppConfigBundle> configBundleSource, DataSource<InputDataBundle> inputDataBundleSource, boolean suppressAnyOutput) {
        return configureAndExecute(configBundleSource, inputDataBundleSource, !suppressAnyOutput, !suppressAnyOutput, !suppressAnyOutput);
    }

    public static SPECpp<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> configureAndExecute(DataSource<SPECppConfigBundle> configBundleSource, DataSource<InputDataBundle> inputDataBundleSource, boolean allowPrinting, boolean allowVisualOutput, boolean allowSaving) {
        SPECppConfigBundle configBundle = configBundleSource.getData();
        InputDataBundle inputDataBundle = inputDataBundleSource.getData();

        preSetup(configBundle, inputDataBundle, allowPrinting);
        SPECpp<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP = setup(configBundle, inputDataBundle);
        postSetup(specPP, allowPrinting);

        execute(specPP, allowPrinting);

        PostSpecOps.postExecution(specPP, allowPrinting, allowVisualOutput, allowSaving);

        return specPP;
    }

    private static void postSetup(SPECpp<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP, boolean allowPrinting) {
        DataSourceCollection parameters = specPP.getGlobalComponentRepository().parameters();
        String x = parameters.toString();
        OutputPathParameters outputPathParameters = parameters.askForData(ParameterRequirements.OUTPUT_PATH_PARAMETERS);
        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.MISC_EXPORT, "parameters", ".txt");
        FileUtils.saveString(filePath, x);
        if (allowPrinting) System.out.println(x);
    }

    private static void preSetup(SPECppConfigBundle configBundle, InputDataBundle data, boolean allowPrinting) {
        if (!allowPrinting) return;
        System.out.println("Executing: " + configBundle.getTitle());
        Log log = data.getLog();
        int traceCount = log.totalTraceCount();
        int variantCount = log.variantCount();
        Set<Activity> activities = data.getMapping().keySet();
        int activityCount = activities.size();
        System.out.println("Traces: " + traceCount + "\tVariants: " + variantCount + "\t Activities: " + activityCount);
        System.out.println("Top 7 variants:");
        log.stream()
           .sorted(Comparator.comparingInt(ii -> -log.getVariantFrequency(ii.getIndex())))
           .limit(7)
           .forEach(ii -> System.out.println(log.getVariantFrequency(ii.getIndex()) + "\t" + ii.getVariant()));
    }


    public static SPECpp<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> setup(SPECppConfigBundle configBundle, InputDataBundle dataBundle) {
        GlobalComponentRepository cr = new GlobalComponentRepository();

        configBundle.instantiate(cr, dataBundle);

        Configuration configuration = new Configuration(cr);
        SPECpp<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specpp = configuration.createFrom(new SPECppBuilder<>(), cr);

        specpp.init();

        return specpp;
    }

    public static void execute(SPECpp<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specpp, boolean allowPrinting) {
        if (allowPrinting) {
            System.out.println("# Commencing SpecOps @" + LocalDateTime.now());
            System.out.println("// ========================================= //");
        }
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();

            specpp.start();

            CompletableFuture<ProMPetrinetWrapper> future = specpp.future(executorService);

            for (ProvidesOngoingVisualization<?> ongoingVisualization : getOngoingVisualizations(specpp)) {
                VizUtils.showVisualization(ongoingVisualization.getOngoingVisualization());
            }

            future.get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            if (allowPrinting) {
                System.out.println("// ========================================= //");
                System.out.println("# Shutting Down SpecOps @" + LocalDateTime.now());
            }
            specpp.stop();
        }
        if (allowPrinting) System.out.println("# Shutdown SpecOps @" + LocalDateTime.now());
    }

    private static List<ProvidesOngoingVisualization<?>> getOngoingVisualizations(SPECpp<?, ?, ?, ?> specpp) {
        return PostSpecOps.getMonitorStream(specpp)
                          .filter(m -> m instanceof ProvidesOngoingVisualization)
                          .map(m -> (ProvidesOngoingVisualization<?>) m)
                          .collect(Collectors.toList());
    }


}
