package org.processmining.specpp.headless;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.plugins.pnml.base.FullPnmlElementFactory;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.orchestra.SPECppConfigBundle;
import org.processmining.specpp.orchestra.SPECppOperations;
import org.processmining.specpp.preprocessing.InputData;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.preprocessing.orderings.AverageFirstOccurrenceIndex;

import java.io.*;

public class ProMLessSPECpp {

    public static void main(String[] args) {
        if (args.length < 2) return;
        String eventLogPath = args[0] + ".xes";
        String resultPath = args[1] + ".pnml";

        run(eventLogPath, resultPath);
    }

    public static void run(String inputLogPath, String outputPetrinetPath) {
        run(inputLogPath, outputPetrinetPath, new PreProcessingParameters(new XEventNameClassifier(), false, AverageFirstOccurrenceIndex.class), CodeDefinedConfigurationSample.createConfiguration());
    }

    public static void run(String inputLogPath, String outputPetrinetPath, PreProcessingParameters preProcessingParameters, SPECppConfigBundle configBundle) {
        InputDataBundle data = InputData.loadData(inputLogPath, preProcessingParameters)
                                        .getData();
        //SPECpp<Place, StatefulPlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = SPECppOperations.configureAndExecute(configBundle, data, false);
        //ProMPetrinetWrapper result = specpp.getPostProcessedResult();
        SPECpp<Place, StatefulPlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = SPECppOperations.setup(configBundle, data);
        ProMPetrinetWrapper result = SPECppOperations.execute(specpp, false);

        myPnmlExport(new File(outputPetrinetPath), result);
    }


    public static void myPnmlExport(File file, AcceptingPetriNet apn) {
        String s = convertToPnmlString(apn);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(s);
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertToPnmlString(AcceptingPetriNet apn) {
        Pnml pnml = new Pnml();
        FullPnmlElementFactory factory = new FullPnmlElementFactory();
        Pnml.setFactory(factory);
        GraphLayoutConnection graphLayoutConnection = new GraphLayoutConnection(apn.getNet());
        pnml = pnml.convertFromNet(apn.getNet(), apn.getInitialMarking(), apn.getFinalMarkings(), graphLayoutConnection);
        pnml.setType(Pnml.PnmlType.PNML);
        return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + pnml.exportElement(pnml);
    }

}
