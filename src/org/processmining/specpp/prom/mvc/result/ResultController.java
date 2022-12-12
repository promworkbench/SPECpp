package org.processmining.specpp.prom.mvc.result;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.log.utils.XUtils;
import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.Factory;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;
import org.processmining.specpp.evaluation.fitness.DetailedFitnessEvaluation;
import org.processmining.specpp.config.SPECppConfigBundle;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;
import org.processmining.specpp.prom.mvc.config.ProMConfig;
import org.processmining.specpp.util.PlaceMaker;

import javax.swing.*;

import static org.processmining.specpp.componenting.data.DataRequirements.dataSource;

public class ResultController extends AbstractStageController {

    private final GlobalComponentRepository gcr;
    private final PlaceMaker placeMaker;
    private final Evaluator<Place, DetailedFitnessEvaluation> fitnessEvaluator;
    private final Evaluator<Place, VariantMarkingHistories> logHistoryMaker;
    private final Log log;
    private final XLog rawLog;
    private XLog evalLog;
    private IntVector variantFrequencies;

    public ResultController(SPECppController parentController) {
        super(parentController);
        rawLog = parentController.cache().askForData(dataSource("raw_log", XLog.class));
        InputDataBundle dataBundle = parentController.getDataBundle();
        SPECppConfigBundle configBundle = parentController.getConfigBundle();

        gcr = new GlobalComponentRepository();
        configBundle.instantiate(gcr, dataBundle);
        placeMaker = new PlaceMaker(dataBundle.getTransitionEncodings());
        EvaluatorConfiguration evConfig = gcr.dataSources().askForData(DataRequirements.EVALUATOR_CONFIG);
        evConfig.createEvaluators();
        fitnessEvaluator = gcr.evaluators().askForEvaluator(EvaluationRequirements.DETAILED_FITNESS);
        logHistoryMaker = gcr.evaluators().askForEvaluator(EvaluationRequirements.PLACE_MARKING_HISTORY);
        variantFrequencies = gcr.dataSources().askForData(DataRequirements.VARIANT_FREQUENCIES);
        log = dataBundle.getLog();
    }


    @Override
    public JPanel createPanel() {
        return new ResultPanel(this, parentController.getResult(), parentController.getIntermediatePostProcessingResults());
    }

    @Override
    public void startup() {

    }

    public ProMPetrinetWrapper getResult() {
        return parentController.getResult();
    }

    public Evaluator<Place, DetailedFitnessEvaluation> getFitnessEvaluator() {
        return fitnessEvaluator;
    }

    public Evaluator<Place, VariantMarkingHistories> getLogHistoryMaker() {
        return logHistoryMaker;
    }

    public PlaceMaker getPlaceMaker() {
        return placeMaker;
    }

    public Log getLog() {
        return log;
    }

    public XLog getRawLog() {
        return rawLog;
    }

    public XLog getEvalLog() {
        if (evalLog == null)
            if (parentController.getInputProcessingConfig() != null && parentController.getInputProcessingConfig()
                                                                                       .getPreProcessingParameters()
                                                                                       .isAddStartEndTransitions()) {
                XFactoryNaiveImpl xFactorY = new XFactoryNaiveImpl();
                XAttributeMap attributeMap = xFactorY.createAttributeMap();
                attributeMap.put("concept:name", xFactorY.createAttributeLiteral("concept:name", Factory.UNIQUE_START_LABEL, XConceptExtension.instance()));
                XEvent startEvent = xFactorY.createEvent(attributeMap);
                attributeMap = xFactorY.createAttributeMap();
                attributeMap.put("concept:name", xFactorY.createAttributeLiteral("concept:name", Factory.UNIQUE_END_LABEL, XConceptExtension.instance()));
                XEvent endEvent = xFactorY.createEvent(attributeMap);
                XLog copiedLog = XUtils.cloneLogWithoutGlobalsAndClassifiers(getRawLog());
                for (XTrace trace : copiedLog) {
                    trace.add(0, startEvent);
                    trace.add(endEvent);
                }
                evalLog = copiedLog;
            } else evalLog = rawLog;
        return evalLog;
    }

    public IntVector getVariantFrequencies() {
        return variantFrequencies;
    }

    public XEventClassifier getEventClassifier() {
        return parentController.getInputProcessingConfig().getPreProcessingParameters().getEventClassifier();
    }

    public ProMConfig getProMConfig() {
        return parentController.getProMConfig();
    }
}
