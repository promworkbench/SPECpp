package org.processmining.specpp.prom.mvc.config;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.config.parameters.ImplicitnessTestingParameters;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.vectorization.OrderingRelation;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.mvc.swing.*;
import org.processmining.specpp.util.PathTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.processmining.specpp.prom.mvc.swing.SwingFactory.html;
import static org.processmining.specpp.prom.mvc.swing.SwingFactory.resizeComboBox;

public class ConfigurationPanel extends AbstractStagePanel<ConfigurationController> {


    private final JComboBox<Preset> presetComboBox;
    private final JComboBox<ProMConfig.SupervisionSetting> supervisionComboBox;
    private final JCheckBox trackCandidateTreeCheckBox;
    private final JComboBox<ProMConfig.TreeExpansionSetting> expansionStrategyComboBox;
    private final JComboBox<FrameworkBridge.AnnotatedTreeHeuristic> heuristicComboBox;
    private final JCheckBox respectWiringCheckBox;
    private final JCheckBox supportRestartCheckBox;
    private final JCheckBox concurrentReplayCheckBox;
    private final JComboBox<FrameworkBridge.AnnotatedEvaluator> deltaAdaptationFunctionComboBox;
    private final JComboBox<ProMConfig.CompositionStrategy> compositionStrategyComboBox;
    private final MyListModel<FrameworkBridge.AnnotatedPostProcessor> ppPipelineModel;
    private final JTextField tauField;
    private final JTextField deltaField;
    private final JTextField steepnessField;
    private final JTextField depthField;
    private final LabeledComboBox<FrameworkBridge.AnnotatedTreeHeuristic> bridgedHeuristicsLabeledComboBox;
    private final LabeledComboBox<FrameworkBridge.AnnotatedEvaluator> deltaAdaptationLabeledComboBox;
    private static final Predicate<JTextField> zeroOneDoublePredicate = input -> {
        try {
            double v = Double.parseDouble(input.getText());
            return 0.0 <= v && v <= 1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    };

    private static final Function<String, Double> zeroOneDoubleFunc = s -> {
        double v = Double.parseDouble(s);
        return (0.0 <= v && v <= 1.0) ? v : null;
    };
    private static final Function<String, Double> doubleFunc = Double::parseDouble;
    private static final Function<String, Integer> posIntFunc = s -> {
        int v = Integer.parseInt(s);
        return (v > 0) ? v : null;
    };
    private static final Function<String, Duration> durationFunc = Duration::parse;

    private static final Predicate<JTextField> posIntPredicate = input -> {
        if (!input.isEnabled()) return true;
        try {
            double v = Integer.parseInt(input.getText());
            return 0 < v;
        } catch (NumberFormatException e) {
            return false;
        }
    };
    private static final Predicate<JTextField> durationStringPredicate = input -> {
        if (!input.isEnabled()) return true;
        try {
            Duration.parse(input.getText());
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    };
    private final TextBasedInputField<Double> tauInput;
    private final TextBasedInputField<Double> deltaInput;
    private final TextBasedInputField<Integer> steepnessInput;
    private final ActivatableTextBasedInputField<Integer> depthInput;
    private final ActivatableTextBasedInputField<Duration> discoveryTimeLimitInput;
    private final ActivatableTextBasedInputField<Duration> totalTimeLimitInput;
    private final JButton runButton;
    private final JCheckBox permitSubtreeCutoffCheckBox;
    private final JCheckBox logToFileCheckBox;
    private final CheckboxedComboBox<ImplicitnessReplayRestriction> restrictReplayBasedImplicitnessInput;
    private final JCheckBox permitNegativeMarkingsCheckBox;
    private final JCheckBox enforceHeuristicScoreThresholdCheckBox;
    private final ComboBoxAndTextBasedInputField<Double, OrderingRelation> heuristicThresholdInput;
    private final CheckboxedComboBox<ProMConfig.CIPRVariant> ciprVariantCheckboxedComboBox;
    private final JCheckBox checkBoxETCBasedComposer;
    private final TextBasedInputField<Double> rhoInput;
    private final JCheckBox logHeuristicsCheckBox;
    private final JCheckBox initiallyWireSelfLoopsCheckBox = SwingFactory.labeledCheckBox("initially wire self loops", false);
    private final HorizontalJPanel deltaRelatedParametersPanel;

    @Override
    public boolean isValidateRoot() {
        return true; // super.isValidateRoot();
    }

    public ConfigurationPanel(ConfigurationController controller) {
        super(controller, new GridBagLayout());

        // ** SUPERVISION ** //

        TitledBorderScrollPanel supervision = new TitledBorderScrollPanel("Preset & Supervision");
        Preset[] availablePresets = Preset.values();
        if (controller.getParentController().getLoadedConfig() == null)
            availablePresets = Arrays.copyOf(Preset.values(), Preset.values().length - 1);
        LabeledComboBox<Preset> presetLabeledComboBox = SwingFactory.labeledComboBox("Configuration Preset", availablePresets);
        presetComboBox = presetLabeledComboBox.getComboBox();
        presetComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedPreset();
        });
        presetLabeledComboBox.add(SwingFactory.help("A preset resets all subsequently configured options.", () -> "Default - the default\nLightweight - a config with which sacrifices supervision against performance\nLast - the previously executed config\nLoaded - (optional) available if this plugin was applied to a log AND previously exported config"));
        supervision.append(presetLabeledComboBox);
        LabeledComboBox<ProMConfig.SupervisionSetting> supervisionSettingLabeledComboBox = SwingFactory.labeledComboBox("Level of Detail of Supervision", ProMConfig.SupervisionSetting.values());
        supervisionComboBox = supervisionSettingLabeledComboBox.getComboBox();
        SwingFactory.resizeComboBox(supervisionComboBox, 200, 25);
        supervisionComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedSupervisionSettings();
        });
        supervision.append(supervisionSettingLabeledComboBox);
        supervisionSettingLabeledComboBox.add(SwingFactory.help(null, "Determines whether event generating implementations of the subsequent configured components will be used.\nEvent generation increases overhead but greatly benefits behavior analysis of the configured algorithm."));
        logToFileCheckBox = SwingFactory.labeledCheckBox("log to file");
        logToFileCheckBox.setSelected(false);
        String s = OutputPathParameters.getDefault().getFilePath(PathTools.OutputFileType.MAIN_LOG, "main");
        logToFileCheckBox.setToolTipText(String.format("Whether to setup a file logger to \"%s\"", s));
        logToFileCheckBox.addChangeListener(e -> updatedSupervisionSettings());
        HorizontalJPanel logToWhatPanel = new HorizontalJPanel();
        logHeuristicsCheckBox = SwingFactory.labeledCheckBox("log heuristics");
        logHeuristicsCheckBox.setSelected(false);
        logHeuristicsCheckBox.addChangeListener(e -> updatedSupervisionSettings());
        logHeuristicsCheckBox.setVisible(false);
        logToWhatPanel.add(logToFileCheckBox);
        logToWhatPanel.add(logHeuristicsCheckBox);
        supervision.append(logToWhatPanel);
        trackCandidateTreeCheckBox = SwingFactory.labeledCheckBox("track candidate tree");
        trackCandidateTreeCheckBox.addChangeListener(e -> updatedSupervisionSettings());
        //supervision.append(trackCandidateTreeCheckBox);
        supervision.completeWithWhitespace();

        // ** PROPOSAL ** //

        TitledBorderScrollPanel proposal = new TitledBorderScrollPanel("Proposal");
        LabeledComboBox<ProMConfig.TreeExpansionSetting> candidateEnumerationLabeledComboBox = SwingFactory.labeledComboBox("Place Enumeration", ProMConfig.TreeExpansionSetting.values());
        expansionStrategyComboBox = candidateEnumerationLabeledComboBox.getComboBox();
        candidateEnumerationLabeledComboBox.add(SwingFactory.help("The strategy by which the place candidate tree is traversed.", "BFS - breadth first search\nDFS - depth first search\nHeuristic - using the subsequently configured heuristic"));
        proposal.append(candidateEnumerationLabeledComboBox);
        bridgedHeuristicsLabeledComboBox = SwingFactory.labeledComboBox("Heuristic", FrameworkBridge.HEURISTICS.toArray(new FrameworkBridge.AnnotatedTreeHeuristic[0]));
        heuristicComboBox = bridgedHeuristicsLabeledComboBox.getComboBox();
        SwingFactory.resizeComboBox(heuristicComboBox, 150, 25);
        heuristicComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedProposalSettings();
        });
        bridgedHeuristicsLabeledComboBox.setVisible(false);
        bridgedHeuristicsLabeledComboBox.add(SwingFactory.help(null, html("Place Interestingness - based on eventually follows relation of activities (see <a href=\"https://dx.doi.org/10.1007/978-3-030-66498-5_25\">Improving the State-Space Traversal of the eST-Miner by Exploiting Underlying Log Structures</a>)<br>BFS Emulation - equals depth(place)<br>DFS Emulation - equals -depth(place)")));
        proposal.append(bridgedHeuristicsLabeledComboBox);
        enforceHeuristicScoreThresholdCheckBox = SwingFactory.labeledCheckBox("enforce heuristic score threshold");
        enforceHeuristicScoreThresholdCheckBox.addActionListener(e -> updatedProposalSettings());
        enforceHeuristicScoreThresholdCheckBox.setVisible(false);
        enforceHeuristicScoreThresholdCheckBox.setToolTipText("Whether to discard places in the candidate tree, and thus also their subtrees, which do not meet the threshold.");
        proposal.append(enforceHeuristicScoreThresholdCheckBox);
        permitSubtreeCutoffCheckBox = SwingFactory.labeledCheckBox("Permit over/underfed subtree cutoff");
        permitSubtreeCutoffCheckBox.addChangeListener(e -> updatedProposalSettings());
        // proposal.append(permitSubtreeCutoffCheckBox)
        respectWiringCheckBox = SwingFactory.labeledCheckBox("respect wiring constraints");
        respectWiringCheckBox.addChangeListener(e -> updatedProposalSettings());
        respectWiringCheckBox.setToolTipText("Whether to integrate wiring constraints into the candidate tree traversal.");
        proposal.append(respectWiringCheckBox);
        supportRestartCheckBox = SwingFactory.labeledCheckBox("use restartable implementation");
        supportRestartCheckBox.addChangeListener(e -> updatedProposalSettings());
        proposal.completeWithWhitespace();

        // ** EVALUATION ** //

        TitledBorderScrollPanel evaluation = new TitledBorderScrollPanel("Evaluation");
        concurrentReplayCheckBox = SwingFactory.labeledCheckBox("use parallel replay implementation");
        concurrentReplayCheckBox.addChangeListener(e -> updatedEvaluationSettings());
        concurrentReplayCheckBox.setToolTipText("Whether to favor a parallel (over variants) replay implementation. Influences performance.");
        evaluation.append(concurrentReplayCheckBox);

        permitNegativeMarkingsCheckBox = SwingFactory.labeledCheckBox("permit negative markings during token-based replay");
        permitNegativeMarkingsCheckBox.addChangeListener(e -> updatedEvaluationSettings());
        permitNegativeMarkingsCheckBox.setToolTipText("Whether the token count during replay is clipped at zero, thus changing the overfed semantics.");
        evaluation.append(permitNegativeMarkingsCheckBox);

        restrictReplayBasedImplicitnessInput = SwingFactory.checkboxedComboBox("restrict replay-based implicitness testing", false, ImplicitnessReplayRestriction.values());
        restrictReplayBasedImplicitnessInput.getCheckBox().addChangeListener(e -> updatedEvaluationSettings());
        resizeComboBox(restrictReplayBasedImplicitnessInput.getComboBox(), 250, 25);
        restrictReplayBasedImplicitnessInput.getComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedEvaluationSettings();
        });
        restrictReplayBasedImplicitnessInput.getComboBox()
                                            .setRenderer(createTooltippedListCellRenderer(ImmutableMap.of(ImplicitnessReplayRestriction.FittingOnAcceptedPlaces, "Replay is restricted to the variants which all previously accepted places, plus the new candidate, are replayable on.", ImplicitnessReplayRestriction.FittingOnEvaluatedPair, "Replay is restricted to the variants on which the new candidate and the singular place it is compared to are replayable on.")));
        restrictReplayBasedImplicitnessInput.getCheckBox()
                                            .setToolTipText("When replay is restricted to a fitting sub log, more places will be marked as implicit.");
        restrictReplayBasedImplicitnessInput.setVisible(false);
        evaluation.append(restrictReplayBasedImplicitnessInput);

        deltaAdaptationLabeledComboBox = SwingFactory.labeledComboBox("Delta Adaptation Function", FrameworkBridge.DELTA_FUNCTIONS.toArray(new FrameworkBridge.AnnotatedEvaluator[0]));
        deltaAdaptationFunctionComboBox = deltaAdaptationLabeledComboBox.getComboBox();
        deltaAdaptationFunctionComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedEvaluationSettings();
        });
        deltaAdaptationLabeledComboBox.setVisible(false);
        String tauDeltaLinkHtml = "<a href=\"https://www.researchgate.net/publication/359791457_Discovering_Process_Models_With_Long-Term_Dependencies_While_Providing_Guarantees_and_Handling_Infrequent_Behavior\">Discovering Process Models With Long-Term Dependencies While Providing Guarantees and Handling Infrequent Behavior</a>";
        deltaAdaptationLabeledComboBox.add(SwingFactory.help(null, html("None - equal to delta=1<br>Constant - delta is not adapted<br>Linear - delta is adapted linearly along the maximum tree depth using the steepness parameter<br>Sigmoid - delta is adapted using a sigmoid function parameterized by the steepness<br>see " + tauDeltaLinkHtml + " for details")));
        evaluation.append(deltaAdaptationLabeledComboBox);
        evaluation.completeWithWhitespace();

        // ** COMPOSITION ** //

        TitledBorderScrollPanel composition = new TitledBorderScrollPanel("Composition");
        LabeledComboBox<ProMConfig.CompositionStrategy> compositionStrategyLabeledComboBox = SwingFactory.labeledComboBox("Variant", ProMConfig.CompositionStrategy.values());
        compositionStrategyComboBox = compositionStrategyLabeledComboBox.getComboBox();
        compositionStrategyComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedCompositionSettings();
        });
        compositionStrategyLabeledComboBox.add(SwingFactory.help("Determines the composition strategy.", html("Standard - Candidate places are simply filtered according to the fitness threshold tau<br>Tau-Delta - see " + tauDeltaLinkHtml + " <br>Uniwired - see <a href=\"https://dx.doi.org/10.1007/978-3-030-37453-2_19\">Finding Uniwired Petri Nets Using eST-Miner</a>")));
        composition.append(compositionStrategyLabeledComboBox);
        ciprVariantCheckboxedComboBox = SwingFactory.checkboxedComboBox("apply concurrent implicit place removal", true, new ProMConfig.CIPRVariant[]{ProMConfig.CIPRVariant.ReplayBased, ProMConfig.CIPRVariant.LPBased});
        ciprVariantCheckboxedComboBox.getCheckBox().addChangeListener(e -> updatedCompositionSettings());
        ciprVariantCheckboxedComboBox.getCheckBox()
                                     .setToolTipText("Whether only non-implicit places are accepted into the current result.");
        ciprVariantCheckboxedComboBox.getComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedCompositionSettings();
        });
        ciprVariantCheckboxedComboBox.getComboBox()
                                     .setRenderer(createTooltippedListCellRenderer(ImmutableMap.of(ProMConfig.CIPRVariant.ReplayBased, "Uses subregion implicitness on the markings obtained from replay.", ProMConfig.CIPRVariant.LPBased, "Uses lp optimization based structural implicitness.")));
        composition.append(ciprVariantCheckboxedComboBox);
        checkBoxETCBasedComposer = SwingFactory.labeledCheckBox("use ETC-based composition", false);
        checkBoxETCBasedComposer.setToolTipText("Whether to use ETC-based composition which is incompatible with CIPR.");
        checkBoxETCBasedComposer.addItemListener(e -> updatedCompositionSettings());

        //make cipr and ETC-based composer mutually exclusive
        ciprVariantCheckboxedComboBox.getCheckBox().addActionListener(e -> checkBoxETCBasedComposer.setSelected(false));
        checkBoxETCBasedComposer.addActionListener(e -> ciprVariantCheckboxedComboBox.getCheckBox().setSelected(false));

        composition.append(checkBoxETCBasedComposer);
        composition.completeWithWhitespace();

        // ** POST PROCESSING ** //

        TitledBorderPanel postProcessing = new TitledBorderPanel("Post Processing", new BorderLayout());
        ppPipelineModel = new MyListModel<>();
        postProcessing.add(new PostProcessingConfigPanel(controller.getContext(), ppPipelineModel), BorderLayout.CENTER);
        postProcessing.add(Box.createVerticalGlue(), BorderLayout.PAGE_END);
        // ** PARAMETERS ** //

        TitledBorderScrollPanel parameters = new TitledBorderScrollPanel("Parameters");
        parameters.setFocusable(true); // focus is still not lost on click outside
        tauInput = SwingFactory.textBasedInputField("tau", zeroOneDoubleFunc, 10);
        tauInput.getTextField().setToolTipText("Minimal place fitness threshold in [0, 1].");
        tauField = tauInput.getTextField();
        Consumer<Boolean> listener = b -> updatedParameters();
        tauInput.addVerificationStatusListener(listener);
        parameters.append(tauInput);

        rhoInput = SwingFactory.textBasedInputField("rho", zeroOneDoubleFunc, 10);
        rhoInput.addVerificationStatusListener(listener);
        //rhoInput.setText("1.0");
        rhoInput.setToolTipText("Precision threshold to abort the search prematurely: rho in [0,1].");
        rhoInput.setVisible(false);
        parameters.append(rhoInput);

        deltaRelatedParametersPanel = new HorizontalJPanel();
        deltaInput = SwingFactory.textBasedInputField("delta", zeroOneDoubleFunc, 10);
        deltaInput.getTextField().setToolTipText("Delta parameter in [0,1].");
        deltaField = deltaInput.getTextField();
        deltaInput.addVerificationStatusListener(listener);
        deltaInput.setVisible(false);
        deltaRelatedParametersPanel.add(deltaInput);
        steepnessInput = SwingFactory.textBasedInputField("steepness", posIntFunc, 10);
        steepnessInput.setToolTipText("Steepness parameter in {1,...}.");
        steepnessInput.addVerificationStatusListener(listener);
        steepnessField = steepnessInput.getTextField();
        steepnessInput.setVisible(false);
        deltaRelatedParametersPanel.addSpaced(steepnessInput);
        deltaRelatedParametersPanel.setVisible(false);
        parameters.append(deltaRelatedParametersPanel);

        heuristicThresholdInput = SwingFactory.comboBoxAndTextBasedInputField("heuristic threshold", OrderingRelation.values(), doubleFunc, 10);
        heuristicThresholdInput.addVerificationStatusListener(listener);
        heuristicThresholdInput.setVisible(false);
        parameters.append(heuristicThresholdInput);

        depthInput = SwingFactory.activatableTextBasedInputField("max depth", false, posIntFunc, 10);
        depthInput.setToolTipText("Max depth to traverse candidate tree to in {1,...}.");
        depthField = depthInput.getTextField();
        depthInput.addVerificationStatusListener(listener);
        parameters.append(depthInput);

        initiallyWireSelfLoopsCheckBox.addChangeListener(e -> updatedParameters());
        initiallyWireSelfLoopsCheckBox.setToolTipText("");
        initiallyWireSelfLoopsCheckBox.setVisible(false);
        parameters.append(initiallyWireSelfLoopsCheckBox);

        parameters.completeWithWhitespace();

        // ** EXECUTION ** //

        TitledBorderPanel execution = new TitledBorderPanel("Execution");
        execution.setFocusable(true);
        HorizontalJPanel timeLimitsPanel = new HorizontalJPanel();
        discoveryTimeLimitInput = SwingFactory.activatableTextBasedInputField("discovery time limit", false, durationFunc, 25);
        discoveryTimeLimitInput.getCheckBox()
                               .setToolTipText("Real time limit for place discovery. Gracefully continues to post processing with intermediate result.");
        discoveryTimeLimitInput.getTextField()
                               .setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
        discoveryTimeLimitInput.addVerificationStatusListener(listener);
        timeLimitsPanel.add(discoveryTimeLimitInput);
        totalTimeLimitInput = SwingFactory.activatableTextBasedInputField("total time limit", false, durationFunc, 25);
        totalTimeLimitInput.getCheckBox()
                           .setToolTipText("Real time limit over entire computation (discovery + post processing). Stops abruptly.");
        totalTimeLimitInput.getTextField()
                           .setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
        totalTimeLimitInput.addVerificationStatusListener(listener);
        timeLimitsPanel.addSpaced(totalTimeLimitInput);
        execution.append(timeLimitsPanel);

        runButton = SlickerFactory.instance().createButton("run");
        runButton.addActionListener(e -> tryRun());
        execution.append(runButton);
        execution.completeWithWhitespace();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = 0;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(supervision, c);
        c.gridy++;
        add(proposal, c);
        c.gridy++;
        add(evaluation, c);
        c.gridy++;
        add(composition, c);
        c.gridy++;
        add(Box.createHorizontalStrut(650), c);
        c.gridy = 0;
        c.gridx = 1;
        c.gridheight = 2;
        add(postProcessing, c);
        c.gridheight = 1;
        c.gridy += 2;
        add(parameters, c);
        c.gridy++;
        add(execution, c);


        if (controller.getParentController().getProMConfig() != null) presetComboBox.setSelectedItem(Preset.Last);
        else if (controller.getParentController().getLoadedProMConfig() != null)
            presetComboBox.setSelectedItem(Preset.Loaded);
        else {
            presetComboBox.setSelectedItem(Preset.Default);
            updatedPreset();
        }

    }

    private static DefaultListCellRenderer createTooltippedListCellRenderer(Map<Object, String> tooltips) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (tooltips.containsKey(value)) list.setToolTipText(tooltips.get(value));
                return comp;
            }
        };
    }

    public static boolean validatePostProcessingPipeline(MyListModel<FrameworkBridge.AnnotatedPostProcessor> ppPipelineModel) {
        ListIterator<FrameworkBridge.AnnotatedPostProcessor> it = ppPipelineModel.iterator();
        if (!it.hasNext()) return false;
        FrameworkBridge.AnnotatedPostProcessor prev = FrameworkBridge.BridgedPostProcessors.Identity.getBridge();
        while (it.hasNext()) {
            FrameworkBridge.AnnotatedPostProcessor next = it.next();
            if (!next.getInType().isAssignableFrom(prev.getOutType())) return false;
            prev = next;
        }
        return ProMPetrinetWrapper.class.isAssignableFrom(prev.getOutType());
    }

    private void initializeFromProMConfig(ProMConfig pc) {
        supervisionComboBox.setSelectedItem(pc.supervisionSetting);
        expansionStrategyComboBox.setSelectedItem(pc.treeExpansionSetting);
        respectWiringCheckBox.setSelected(pc.respectWiring);
        supportRestartCheckBox.setSelected(pc.supportRestart);
        if (pc.treeHeuristic != null)
            heuristicComboBox.setSelectedItem(pc.treeHeuristic);
        enforceHeuristicScoreThresholdCheckBox.setSelected(pc.enforceHeuristicThreshold);
        concurrentReplayCheckBox.setSelected(pc.concurrentReplay);
        permitNegativeMarkingsCheckBox.setSelected(pc.permitNegativeMarkingsDuringReplay);
        restrictReplayBasedImplicitnessInput.getCheckBox()
                                            .setSelected(pc.implicitnessReplaySubLogRestriction != ImplicitnessTestingParameters.SubLogRestriction.None);
        if (pc.implicitnessReplaySubLogRestriction == ImplicitnessTestingParameters.SubLogRestriction.FittingOnAcceptedPlacesAndEvaluatedPlace)
            restrictReplayBasedImplicitnessInput.getComboBox()
                                                .setSelectedItem(ImplicitnessReplayRestriction.FittingOnAcceptedPlaces);
        else if (pc.implicitnessReplaySubLogRestriction == ImplicitnessTestingParameters.SubLogRestriction.MerelyFittingOnEvaluatedPair)
            restrictReplayBasedImplicitnessInput.getComboBox()
                                                .setSelectedItem(ImplicitnessReplayRestriction.FittingOnEvaluatedPair);
        deltaAdaptationFunctionComboBox.setSelectedItem(pc.deltaAdaptationFunction);
        compositionStrategyComboBox.setSelectedItem(pc.compositionStrategy);
        ciprVariantCheckboxedComboBox.getCheckBox().setSelected(pc.ciprVariant != ProMConfig.CIPRVariant.None);
        ciprVariantCheckboxedComboBox.getComboBox()
                                     .setSelectedItem(pc.ciprVariant != ProMConfig.CIPRVariant.None ? pc.ciprVariant : ProMConfig.CIPRVariant.ReplayBased);
        checkBoxETCBasedComposer.setSelected(pc.useETCBasedComposer);

        ppPipelineModel.clear();
        pc.ppPipeline.forEach(ppPipelineModel::append);
        tauInput.setText(Double.toString(pc.tau));
        rhoInput.setText(pc.rho < 0 ? null : Double.toString(pc.rho));
        deltaInput.setText(pc.delta < 0 ? null : Double.toString(pc.delta));
        steepnessInput.setText(pc.steepness < 0 ? null : Integer.toString(pc.steepness));
        if (pc.enforceHeuristicThreshold)
            heuristicThresholdInput.setBoth(pc.heuristicThresholdRelation, Double.toString(pc.heuristicThreshold));
        else heuristicThresholdInput.setBoth(OrderingRelation.gt, null);
        if (pc.depth >= 0) {
            depthInput.setText(Integer.toString(pc.depth));
            depthInput.activate();
        } else
            depthInput.deactivate();
        initiallyWireSelfLoopsCheckBox.setSelected(pc.initiallyWireSelfLoops);
        if (pc.discoveryTimeLimit != null) {
            discoveryTimeLimitInput.setText(pc.discoveryTimeLimit.toString());
            discoveryTimeLimitInput.activate();
        } else discoveryTimeLimitInput.deactivate();
        if (pc.totalTimeLimit != null) {
            totalTimeLimitInput.setText(pc.totalTimeLimit.toString());
            totalTimeLimitInput.activate();
        } else totalTimeLimitInput.deactivate();

        SwingUtilities.invokeLater(() -> {
            updatedCompositionSettings();
            updatedProposalSettings();
            updatedEvaluationSettings();
            updatedParameters();
            updateReadinessState();
        });
    }

    public ProMConfig collectConfig() {
        ProMConfig pc = new ProMConfig();

        pc.supervisionSetting = (ProMConfig.SupervisionSetting) supervisionComboBox.getSelectedItem();
        pc.logToFile = logToFileCheckBox.isSelected();
        pc.logHeuristics = logHeuristicsCheckBox.isSelected();
        pc.treeExpansionSetting = (ProMConfig.TreeExpansionSetting) expansionStrategyComboBox.getSelectedItem();
        pc.respectWiring = respectWiringCheckBox.isSelected();
        pc.supportRestart = supportRestartCheckBox.isSelected();
        pc.treeHeuristic = (FrameworkBridge.AnnotatedTreeHeuristic) heuristicComboBox.getSelectedItem();
        pc.enforceHeuristicThreshold = enforceHeuristicScoreThresholdCheckBox.isSelected();
        pc.concurrentReplay = concurrentReplayCheckBox.isSelected();
        pc.permitNegativeMarkingsDuringReplay = permitNegativeMarkingsCheckBox.isSelected();
        boolean selected = restrictReplayBasedImplicitnessInput.getCheckBox().isSelected();
        if (selected) {
            ImplicitnessReplayRestriction item = (ImplicitnessReplayRestriction) restrictReplayBasedImplicitnessInput.getComboBox()
                                                                                                                     .getSelectedItem();
            if (item == null) return null;
            switch (item) {
                case FittingOnAcceptedPlaces:
                    pc.implicitnessReplaySubLogRestriction = ImplicitnessTestingParameters.SubLogRestriction.FittingOnAcceptedPlacesAndEvaluatedPlace;
                    break;
                case FittingOnEvaluatedPair:
                    pc.implicitnessReplaySubLogRestriction = ImplicitnessTestingParameters.SubLogRestriction.MerelyFittingOnEvaluatedPair;
                    break;
            }
        } else pc.implicitnessReplaySubLogRestriction = ImplicitnessTestingParameters.SubLogRestriction.None;
        pc.deltaAdaptationFunction = (FrameworkBridge.AnnotatedEvaluator) deltaAdaptationFunctionComboBox.getSelectedItem();
        pc.compositionStrategy = (ProMConfig.CompositionStrategy) compositionStrategyComboBox.getSelectedItem();
        pc.ciprVariant = ciprVariantCheckboxedComboBox.getCheckBox()
                                                      .isSelected() ? (ProMConfig.CIPRVariant) ciprVariantCheckboxedComboBox.getComboBox()
                                                                                                                            .getSelectedItem() : ProMConfig.CIPRVariant.None;
        pc.useETCBasedComposer = checkBoxETCBasedComposer.isSelected();
        if (!validatePostProcessingPipeline(ppPipelineModel)) return null;
        pc.ppPipeline = ImmutableList.copyOf(ppPipelineModel.iterator());
        pc.tau = tauInput.getInput() != null ? tauInput.getInput() : -1;
        pc.rho = rhoInput.getInput() != null ? rhoInput.getInput() : -1;
        pc.delta = deltaInput.getInput() != null ? deltaInput.getInput() : -1;
        pc.steepness = steepnessInput.getInput() != null ? steepnessInput.getInput() : -1;
        pc.heuristicThreshold = heuristicThresholdInput.getInput() != null ? heuristicThresholdInput.getInput() : -1;
        pc.heuristicThresholdRelation = heuristicThresholdInput.getSelectedItem();
        pc.depth = depthInput.getInput() != null ? depthInput.getInput() : -1;
        pc.initiallyWireSelfLoops = initiallyWireSelfLoopsCheckBox.isVisible() && initiallyWireSelfLoopsCheckBox.isSelected();
        pc.discoveryTimeLimit = discoveryTimeLimitInput.getInput();
        pc.totalTimeLimit = totalTimeLimitInput.getInput();

        // COLLECTION COMPLETE

        // VALIDATING COMPLETENESS
        if (!pc.validate()) return null;

        return pc;
    }


    private void tryRun() {
        ProMConfig collectedConfig = collectConfig();
        if (collectedConfig != null) controller.basicConfigCompleted(collectedConfig);
        else
            JOptionPane.showMessageDialog(this, "The configuration is invalid; cannot run discovery.", "Running Attempt", JOptionPane.WARNING_MESSAGE);
    }

    private void updatedParameters() {
        updateReadinessState();
    }

    private void updatedCompositionSettings() {
        rhoInput.setVisible(checkBoxETCBasedComposer.isSelected());
        initiallyWireSelfLoopsCheckBox.setVisible(compositionStrategyComboBox.getSelectedItem() == ProMConfig.CompositionStrategy.Uniwired || respectWiringCheckBox.isSelected());
        ciprVariantCheckboxedComboBox.getComboBox()
                                     .setVisible(ciprVariantCheckboxedComboBox.getCheckBox().isSelected());
        restrictReplayBasedImplicitnessInput.setVisible(ciprVariantCheckboxedComboBox.getCheckBox().isSelected() && (ciprVariantCheckboxedComboBox.getComboBox().getSelectedItem() == ProMConfig.CIPRVariant.ReplayBased));
        deltaAdaptationLabeledComboBox.setVisible(compositionStrategyComboBox.getSelectedItem() == ProMConfig.CompositionStrategy.TauDelta);
        changeDeltaParametersVisibility();
        revalidate();
        updateReadinessState();
    }

    private void changeDeltaParametersVisibility() {
        deltaInput.setVisible(compositionStrategyComboBox.getSelectedItem() == ProMConfig.CompositionStrategy.TauDelta && deltaAdaptationFunctionComboBox.getSelectedItem() != FrameworkBridge.BridgedDeltaAdaptationFunctions.None.getBridge());
        steepnessInput.setVisible(compositionStrategyComboBox.getSelectedItem() == ProMConfig.CompositionStrategy.TauDelta && (deltaAdaptationFunctionComboBox.getSelectedItem() == FrameworkBridge.BridgedDeltaAdaptationFunctions.Linear.getBridge() || deltaAdaptationFunctionComboBox.getSelectedItem() == FrameworkBridge.BridgedDeltaAdaptationFunctions.Sigmoid.getBridge()));
        deltaRelatedParametersPanel.setVisible(deltaField.isVisible() || steepnessInput.isVisible());
    }

    private void updatedEvaluationSettings() {
        changeDeltaParametersVisibility();
        restrictReplayBasedImplicitnessInput.getComboBox()
                                            .setVisible(restrictReplayBasedImplicitnessInput.getCheckBox()
                                                                                            .isSelected());
        revalidate();
        updateReadinessState();
    }

    private void updatedProposalSettings() {
        initiallyWireSelfLoopsCheckBox.setVisible(compositionStrategyComboBox.getSelectedItem() == ProMConfig.CompositionStrategy.Uniwired || respectWiringCheckBox.isSelected());
        bridgedHeuristicsLabeledComboBox.setVisible(expansionStrategyComboBox.getSelectedItem() == ProMConfig.TreeExpansionSetting.Heuristic);
        enforceHeuristicScoreThresholdCheckBox.setVisible(expansionStrategyComboBox.getSelectedItem() == ProMConfig.TreeExpansionSetting.Heuristic);
        heuristicThresholdInput.setVisible(expansionStrategyComboBox.getSelectedItem() == ProMConfig.TreeExpansionSetting.Heuristic && enforceHeuristicScoreThresholdCheckBox.isSelected());
        revalidate();
        updateReadinessState();
    }

    private void updatedPreset() {
        Preset preset = (Preset) presetComboBox.getSelectedItem();
        if (preset != null) {
            ProMConfig cfg;
            if (preset == Preset.Last) {
                cfg = controller.getParentController().getProMConfig();
            } else if (preset == Preset.Loaded) {
                cfg = controller.getParentController().getLoadedProMConfig();
            } else cfg = preset.getConfig();
            initializeFromProMConfig(cfg == null ? Preset.Default.getConfig() : cfg);
        }
        updateReadinessState();
    }

    private void updatedSupervisionSettings() {
        if (logToFileCheckBox.isSelected() && supervisionComboBox.getSelectedItem() == ProMConfig.SupervisionSetting.PerformanceAndEvents) {
            logHeuristicsCheckBox.setVisible(true);
        } else {
            logHeuristicsCheckBox.setSelected(false);
            logHeuristicsCheckBox.setVisible(false);
        }
        updateReadinessState();
    }

    private void updateReadinessState() {
        SwingUtilities.invokeLater(() -> {
            ProMConfig pc = collectConfig();
            //System.out.println("ConfigurationPanel.updateReadinessState: " + pc != null);
            runButton.setEnabled(pc != null);
        });
    }

    public enum Preset {
        Default(ProMConfig::getDefault),
        Lightweight(ProMConfig::getLightweight),
        ETC(ProMConfig::getETC),
        TauDelta(ProMConfig::getTauDelta),
        Uniwired(ProMConfig::getUniwired),
        Last(null),
        Loaded(null);

        private final DataSource<ProMConfig> configSource;

        Preset(DataSource<ProMConfig> config) {
            this.configSource = config;
        }

        public ProMConfig getConfig() {
            return configSource.getData();
        }
    }

    public enum ImplicitnessReplayRestriction {
        FittingOnAcceptedPlaces, FittingOnEvaluatedPair
    }
}
