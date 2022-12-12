package org.processmining.specpp.prom.mvc.config;

import com.google.common.collect.ImmutableList;
import org.processmining.specpp.datastructures.vectorization.OrderingRelation;
import org.processmining.specpp.config.parameters.ImplicitnessTestingParameters;
import org.processmining.specpp.prom.alg.FrameworkBridge;

import java.time.Duration;
import java.util.List;

public class ProMConfig {
    SupervisionSetting supervisionSetting;
    boolean logToFile, logHeuristics;
    TreeExpansionSetting treeExpansionSetting;
    boolean respectWiring, supportRestart;
    FrameworkBridge.AnnotatedTreeHeuristic treeHeuristic;
    boolean concurrentReplay, permitNegativeMarkingsDuringReplay;
    ImplicitnessTestingParameters.SubLogRestriction implicitnessReplaySubLogRestriction;
    FrameworkBridge.AnnotatedEvaluator deltaAdaptationFunction;
    public boolean enforceHeuristicThreshold;
    public double heuristicThreshold;
    public OrderingRelation heuristicThresholdRelation;
    CompositionStrategy compositionStrategy;
    public boolean initiallyWireSelfLoops;
    CIPRVariant ciprVariant;
    List<FrameworkBridge.AnnotatedPostProcessor> ppPipeline;
    double tau, delta;
    public int steepness;
    int depth;
    Duration discoveryTimeLimit, totalTimeLimit;

    public ProMConfig() {
    }

    public static ProMConfig getDefault() {
        ProMConfig pc = new ProMConfig();
        pc.supervisionSetting = SupervisionSetting.PerformanceAndEvents;
        pc.logToFile = true;
        pc.logHeuristics = false;
        pc.treeExpansionSetting = TreeExpansionSetting.Heuristic;
        pc.respectWiring = false;
        pc.supportRestart = false;
        pc.treeHeuristic = FrameworkBridge.BridgedHeuristics.BFS_Emulation.getBridge();
        pc.enforceHeuristicThreshold = false;
        pc.concurrentReplay = false;
        pc.permitNegativeMarkingsDuringReplay = false;
        pc.implicitnessReplaySubLogRestriction = ImplicitnessTestingParameters.SubLogRestriction.None;
        pc.deltaAdaptationFunction = FrameworkBridge.BridgedDeltaAdaptationFunctions.Constant.getBridge();
        pc.compositionStrategy = CompositionStrategy.Standard;
        pc.initiallyWireSelfLoops = false;
        pc.ciprVariant = CIPRVariant.ReplayBased;
        pc.ppPipeline = ImmutableList.of(FrameworkBridge.BridgedPostProcessors.LPBasedImplicitPlaceRemoval.getBridge(), FrameworkBridge.BridgedPostProcessors.ProMPetrinetConversion.getBridge());
        pc.tau = 1.0;
        pc.delta = -1.0;
        pc.steepness = -1;
        pc.heuristicThreshold = -1;
        pc.depth = -1;
        pc.discoveryTimeLimit = null;
        pc.totalTimeLimit = null;
        return pc;
    }

    public static ProMConfig getLightweight() {
        ProMConfig pc = getDefault();
        pc.supervisionSetting = SupervisionSetting.Nothing;
        pc.treeExpansionSetting = TreeExpansionSetting.DFS;
        return pc;
    }

    public static ProMConfig getTauDelta() {
        ProMConfig pc = getDefault();
        pc.compositionStrategy = CompositionStrategy.TauDelta;
        pc.deltaAdaptationFunction = FrameworkBridge.BridgedDeltaAdaptationFunctions.Constant.getBridge();
        pc.delta = 1;
        return pc;
    }

    public static ProMConfig getUniwired() {
        ProMConfig pc = getDefault();
        pc.compositionStrategy = CompositionStrategy.Uniwired;
        pc.respectWiring = true;
        pc.initiallyWireSelfLoops = true;
        pc.ppPipeline = ImmutableList.of(FrameworkBridge.BridgedPostProcessors.UniwiredSelfLoopAddition.getBridge(), FrameworkBridge.BridgedPostProcessors.LPBasedImplicitPlaceRemoval.getBridge(), FrameworkBridge.BridgedPostProcessors.ProMPetrinetConversion.getBridge());
        return pc;
    }

    public boolean validate() {
        boolean outOfRange = tau < 0 || tau > 1.0;
        boolean incomplete = (supervisionSetting == null | treeExpansionSetting == null | compositionStrategy == null);
        incomplete |= logHeuristics && (!logToFile || supervisionSetting != SupervisionSetting.PerformanceAndEvents);
        incomplete |= treeExpansionSetting == TreeExpansionSetting.Heuristic && treeHeuristic == null;
        incomplete |= enforceHeuristicThreshold && (heuristicThreshold < 0 || heuristicThresholdRelation == null);
        incomplete |= compositionStrategy == CompositionStrategy.TauDelta && (deltaAdaptationFunction == null || (deltaAdaptationFunction != FrameworkBridge.BridgedDeltaAdaptationFunctions.None.getBridge() && delta < 0) || ((deltaAdaptationFunction == FrameworkBridge.BridgedDeltaAdaptationFunctions.Linear.getBridge() || deltaAdaptationFunction == FrameworkBridge.BridgedDeltaAdaptationFunctions.Sigmoid.getBridge()) && steepness < 0));
        return !outOfRange && !incomplete;
    }

    public interface DisplayableEnum {

        String getDisplayText();

        String getDescription();

    }

    public enum SupervisionSetting implements DisplayableEnum {
        Nothing("Nothing", "Nothing is tracked. Least amount of overhead."),
        PerformanceOnly("Performance Only", "Component's performance is logged."),
        PerformanceAndEvents("Performance & Events", "Utilizes event-generating implementations in addition to performance logging.");


        private final String displayName, description;

        SupervisionSetting(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        @Override
        public String getDisplayText() {
            return displayName;
        }

        @Override
        public String getDescription() {
            return description;
        }

    }

    public enum TreeExpansionSetting {
        BFS, DFS, Heuristic
    }

    public enum CompositionStrategy implements DisplayableEnum {
        Standard("Standard", ""), TauDelta("Tau-Delta", ""), Uniwired("Uniwired", "");

        private final String displayName;
        private final String description;

        CompositionStrategy(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        @Override
        public String getDisplayText() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum CIPRVariant {
        None(ImplicitnessTestingParameters.CIPRVersion.None),
        ReplayBased(ImplicitnessTestingParameters.CIPRVersion.ReplayBased),
        LPBased(ImplicitnessTestingParameters.CIPRVersion.LPBased);

        private final ImplicitnessTestingParameters.CIPRVersion bridge;

        CIPRVariant(ImplicitnessTestingParameters.CIPRVersion bridge) {
            this.bridge = bridge;
        }

        public ImplicitnessTestingParameters.CIPRVersion bridge() {
            return bridge;
        }
    }

}
