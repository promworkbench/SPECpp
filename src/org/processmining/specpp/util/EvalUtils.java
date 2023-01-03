package org.processmining.specpp.util;

import nl.tue.astar.AStarException;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.utils.XUtils;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.etconformance.ETCAlgorithm;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.pnetreplayer.utils.TransEvClassMappingUtils;
import org.processmining.specpp.config.PreProcessingParameters;
import org.processmining.specpp.datastructures.log.impls.Factory;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EvalUtils {

    public static XLog addStartEndActivities(XLog xLog) {
        XFactoryNaiveImpl xFactorY = new XFactoryNaiveImpl();
        XAttributeMap attributeMap = xFactorY.createAttributeMap();
        attributeMap.put("concept:name", xFactorY.createAttributeLiteral("concept:name", Factory.UNIQUE_START_LABEL, XConceptExtension.instance()));
        XEvent startEvent = xFactorY.createEvent(attributeMap);
        attributeMap = xFactorY.createAttributeMap();
        attributeMap.put("concept:name", xFactorY.createAttributeLiteral("concept:name", Factory.UNIQUE_END_LABEL, XConceptExtension.instance()));
        XEvent endEvent = xFactorY.createEvent(attributeMap);
        XLog copiedLog = XUtils.cloneLogWithoutGlobalsAndClassifiers(xLog);
        for (XTrace trace : copiedLog) {
            trace.add(0, startEvent);
            trace.add(endEvent);
        }
        return copiedLog;
    }

    public static XLog createEvalLog(XLog xLog, PreProcessingParameters preProcessingParameters) {
        if (preProcessingParameters != null && preProcessingParameters.isAddStartEndTransitions())
            return addStartEndActivities(xLog);
        else return xLog;
    }

    public static Set<XEventClass> createEventClasses(XEventClassifier eventClassifier, XLog evalLog) {
        return new HashSet<>(XUtils.createEventClasses(eventClassifier, evalLog).getClasses());
    }

    public static TransEvClassMapping createTransEvClassMapping(XEventClassifier eventClassifier, Set<XEventClass> eventClasses, Petrinet petrinet) {
        return TransEvClassMappingUtils.getInstance().getMapping(petrinet, eventClasses, eventClassifier);
    }

    public static TransEvClassMapping createTransEvClassMapping(XEventClassifier eventClassifier, XLog xLog, Petrinet petrinet) {
        Set<XEventClass> eventClasses = createEventClasses(eventClassifier, xLog);
        return createTransEvClassMapping(eventClassifier, eventClasses, petrinet);
    }

    public static CostBasedCompleteParam getCostBasedCompleteParam(TransEvClassMapping transEvClassMapping, Set<XEventClass> eventClasses, AcceptingPetriNet proMPetrinetWrapper) {
        Map<XEventClass, Integer> mapEvClass2Cost = eventClasses.stream().collect(Collectors.toMap(a -> a, a -> 5));
        mapEvClass2Cost.put(transEvClassMapping.getDummyEventClass(), 5);
        Map<Transition, Integer> mapTrans2Cost = proMPetrinetWrapper.getNet()
                                                                    .getTransitions()
                                                                    .stream()
                                                                    .collect(Collectors.toMap(t -> t, t -> 2));
        CostBasedCompleteParam paramObj = new CostBasedCompleteParam(mapEvClass2Cost, mapTrans2Cost);
        paramObj.setMaxNumOfStates(Integer.MAX_VALUE);
        paramObj.setInitialMarking(proMPetrinetWrapper.getInitialMarking());
        paramObj.setFinalMarkings(proMPetrinetWrapper.getFinalMarkings().toArray(new Marking[0]));
        paramObj.setGUIMode(false);
        paramObj.setCreateConn(false);
        return paramObj;
    }


    public static PNRepResult computeAlignmentBasedReplay(PluginContext context, EvaluationLogData evaluationLogData, TransEvClassMapping evClassMapping, ProMPetrinetWrapper proMPetrinetWrapper) throws AStarException {
        CostBasedCompleteParam paramObj = getCostBasedCompleteParam(evClassMapping, evaluationLogData.getEventClasses(), proMPetrinetWrapper);
        return new PNLogReplayer().replayLog(context, proMPetrinetWrapper, evaluationLogData.getEvalLog(), evClassMapping, new PetrinetReplayerWithoutILP(), paramObj);
    }

    public static double computeAlignmentBasedFitness(PluginContext context, EvaluationLogData evaluationLogData, TransEvClassMapping evClassMapping, ProMPetrinetWrapper proMPetrinetWrapper) throws AStarException {
        return ((Double) computeAlignmentBasedReplay(context, evaluationLogData, evClassMapping, proMPetrinetWrapper).getInfo()
                                                                                                                     .get(PNRepResult.TRACEFITNESS));
    }

    public static double deriveAlignmentBasedFitness(PNRepResult replayResults) {
        return (Double) replayResults.getInfo().get(PNRepResult.TRACEFITNESS);
    }

    public static double derivePerfectlyFitting(EvaluationLogData evaluationLogData, PNRepResult replayResults) {
        int perfectlyFittingTraces = replayResults.stream()
                                                  .filter(srr -> srr.getInfo().get(PNRepResult.TRACEFITNESS) == 1)
                                                  .mapToInt(srr -> srr.getTraceIndex().size())
                                                  .sum();
        double size = evaluationLogData.getEvalLog().size();
        return perfectlyFittingTraces / size;
    }

    public static ETCResults computeETC(PluginContext childContext, EvaluationLogData evaluationLogData, TransEvClassMapping evClassMapping, ProMPetrinetWrapper proMPetrinetWrapper) throws Exception {
        ETCResults etcResults = new ETCResults();
        ETCAlgorithm.exec(childContext, evaluationLogData.getEvalLog(), proMPetrinetWrapper, proMPetrinetWrapper.getInitialMarking(), evClassMapping, etcResults);
        return etcResults;
    }

    public static double computeETCPrecision(PluginContext childContext, EvaluationLogData evaluationLogData, TransEvClassMapping evClassMapping, ProMPetrinetWrapper proMPetrinetWrapper) throws Exception {
        return computeETC(childContext, evaluationLogData, evClassMapping, proMPetrinetWrapper).getEtcp();
    }

    public static double deriveETCPrecision(ETCResults etcResults) {
        return etcResults.getEtcp();
    }

    public static double computeF1(double fitness, double precision) {
        double denominator = fitness + precision;
        return denominator == 0 ? Double.NaN : 2 * (fitness * precision) / denominator;
    }

    public static class EvaluationLogData {

        private XLog evalLog;
        private XEventClassifier eventClassifier;
        private Set<XEventClass> eventClasses;

        public EvaluationLogData(XLog evalLog, XEventClassifier eventClassifier, Set<XEventClass> eventClasses) {
            this.evalLog = evalLog;
            this.eventClassifier = eventClassifier;
            this.eventClasses = eventClasses;
        }

        public Set<XEventClass> getEventClasses() {
            return eventClasses;
        }

        public void setEventClasses(Set<XEventClass> eventClasses) {
            this.eventClasses = eventClasses;
        }

        public XLog getEvalLog() {
            return evalLog;
        }

        public void setEvalLog(XLog evalLog) {
            this.evalLog = evalLog;
        }

        public XEventClassifier getEventClassifier() {
            return eventClassifier;
        }

        public void setEventClassifier(XEventClassifier eventClassifier) {
            this.eventClassifier = eventClassifier;
        }
    }
}
