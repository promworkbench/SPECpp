package org.processmining.specpp.prom.mvc.preprocessing;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.google.common.collect.ImmutableList;
import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingStrategy;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.mvc.swing.LabeledComboBox;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class ParametersPanel extends JPanel {

    private final PreProcessingController controller;
    private final JComboBox<XEventClassifier> classifierComboBox;
    private final JComboBox<FrameworkBridge.AnnotatedActivityOrderingStrategy> orderingComboBox;
    private final JCheckBox artificialTransitionsCheckBox;
    private final List<FrameworkBridge.AnnotatedActivityOrderingStrategy> availableOrderings;
    private final ImmutableList<XEventClassifier> availableEventClassifiers;
    private final JButton previewButton;

    public ParametersPanel(PreProcessingController controller, List<XEventClassifier> eventClassifiers) {
        super(new GridBagLayout());
        this.controller = controller;
        PreProcessingParameters defaultParameters = PreProcessingParameters.getDefault();

        availableEventClassifiers = ImmutableList.copyOf(eventClassifiers);
        LabeledComboBox<XEventClassifier> eventClassifierBox = SwingFactory.labeledComboBox("Event Classifier", availableEventClassifiers.toArray(new XEventClassifier[0]));
        classifierComboBox = eventClassifierBox.getComboBox();
        SwingFactory.resizeComboBox(classifierComboBox, 175, 25);
        classifierComboBox.setSelectedItem(defaultParameters.getEventClassifier());

        availableOrderings = FrameworkBridge.ORDERING_STRATEGIES;
        LabeledComboBox<FrameworkBridge.AnnotatedActivityOrderingStrategy> orderingStrategyBox = SwingFactory.labeledComboBox("Ordering Strategy", availableOrderings.toArray(new FrameworkBridge.AnnotatedActivityOrderingStrategy[0]));
        orderingComboBox = orderingStrategyBox.getComboBox();
        SwingFactory.resizeComboBox(orderingComboBox, 250, 25);
        orderingComboBox.setSelectedItem(findEnum(defaultParameters.getTransitionEncodingsBuilderClass()));
        orderingStrategyBox.add(SwingFactory.help(null, SwingFactory.html("Determines the order in which the search tree is explored. Can have a big impact on performance.<br>Refer to <a href=\"https://dx.doi.org/10.1007/978-3-030-66498-5_25\">Improving the State-Space Traversal of the eST-Miner by Exploiting Underlying Log Structures</a>")));

        artificialTransitionsCheckBox = SlickerFactory.instance()
                                                      .createCheckBox("introduce artificial start & end transitions", defaultParameters.isAddStartEndTransitions());


        previewButton = SlickerFactory.instance().createButton("preview");
        previewButton.addActionListener(e -> {
            controller.preview(collectParameters());
        });

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.2;
        add(eventClassifierBox, c);
        c.gridy++;
        add(orderingStrategyBox, c);
        c.gridy++;
        add(artificialTransitionsCheckBox, c);
        c.gridy++;
        c.weighty = 0.2;
        add(previewButton, c);


        tryInstantiatingFromLastOrLoaded();
    }

    private void tryInstantiatingFromLastOrLoaded() {
        PreProcessingParameters preProcessingParameters = controller.getParentController().getPreProcessingParameters();
        if (preProcessingParameters != null) {
            instantiateFrom(preProcessingParameters);
            return;
        }
        PreProcessingParameters loadedPreProcessingParameters = controller.getParentController()
                                                                          .getLoadedPreProcessingParameters();
        if (loadedPreProcessingParameters != null) {
            instantiateFrom(loadedPreProcessingParameters);
        }
    }

    private void instantiateFrom(PreProcessingParameters preProcessingParameters) {
        XEventClassifier eventClassifier = preProcessingParameters.getEventClassifier();
        if (availableEventClassifiers.contains(eventClassifier)) classifierComboBox.setSelectedItem(eventClassifier);
        Class<? extends ActivityOrderingStrategy> aos = preProcessingParameters.getTransitionEncodingsBuilderClass();
        orderingComboBox.setSelectedItem(findEnum(aos));
        artificialTransitionsCheckBox.setSelected(preProcessingParameters.isAddStartEndTransitions());
    }

    private FrameworkBridge.AnnotatedActivityOrderingStrategy findEnum(Class<? extends ActivityOrderingStrategy> strategyClass) {
        Optional<FrameworkBridge.AnnotatedActivityOrderingStrategy> first = availableOrderings.stream()
                                                                                              .filter(ans -> ans.getStrategyClass()
                                                                                                                .equals(strategyClass))
                                                                                              .findFirst();
        return first.orElse(FrameworkBridge.BridgedActivityOrderingStrategies.AverageFirstOccurrenceIndex.getBridge());
    }

    public PreProcessingParameters collectParameters() {
        XEventClassifier eventClassifier = availableEventClassifiers.get(classifierComboBox.getSelectedIndex());
        Class<? extends ActivityOrderingStrategy> orderingStrategy = ((FrameworkBridge.AnnotatedActivityOrderingStrategy) orderingComboBox.getSelectedItem()).getStrategyClass();
        boolean introduceArtificialTransitions = artificialTransitionsCheckBox.isSelected();
        return new PreProcessingParameters(eventClassifier, introduceArtificialTransitions, orderingStrategy);
    }

    public void disableButton() {
        SwingUtilities.invokeLater(() -> previewButton.setEnabled(false));
    }

    public void enableButton() {
        SwingUtilities.invokeLater(() -> previewButton.setEnabled(true));
    }
}
