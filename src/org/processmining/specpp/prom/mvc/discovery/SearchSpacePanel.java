package org.processmining.specpp.prom.mvc.discovery;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.prom.util.Destructible;

import javax.swing.*;
import java.math.BigInteger;

public class SearchSpacePanel extends JPanel implements Destructible {

    private final SPECpp<Place, AdvancedComposition<Place>, PetriNet, ProMPetrinetWrapper> specpp;
    private final int maxTreeDepth;
    private final BigInteger maxCandidates;
    private final Timer updateTimer;


    public SearchSpacePanel(SPECpp<Place, AdvancedComposition<Place>, PetriNet, ProMPetrinetWrapper> specpp) {
        this.specpp = specpp;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        IntEncodings<Transition> transitionEncodings = specpp.getGlobalComponentRepository()
                                                             .dataSources()
                                                             .askForData(DataRequirements.ENC_TRANS);
        int preSize = transitionEncodings.pre().size();
        int postSize = transitionEncodings.post().size();
        add(SlickerFactory.instance()
                          .createLabel("#Preset Transitions: " + preSize + ", #Postset Transitions: " + postSize));
        BigInteger combinations = BigInteger.valueOf(2).pow(preSize + postSize);
        BigInteger possiblePlaces = BigInteger.valueOf(2)
                                              .pow(preSize + postSize - 2)
                                              .subtract(BigInteger.valueOf(1 + postSize));
        maxCandidates = possiblePlaces;
        add(SlickerFactory.instance()
                          .createLabel("#Possible Combinations: 2^(#Preset Transitions) + 2^(#Postset Transitions) = " + combinations));
        add(SlickerFactory.instance()
                          .createLabel("#Candidate Places: #Possible Combinations/2^2 - 1 - #Postset Transitions = " + possiblePlaces));
        maxTreeDepth = Math.min(specpp.getGlobalComponentRepository()
                                      .parameters()
                                      .askForData(ParameterRequirements.PLACE_GENERATOR_PARAMETERS)
                                      .getMaxTreeDepth(), preSize + postSize);
        JLabel traversedCandidatesLabel = SlickerFactory.instance().createLabel(createTraversedCandidatesString());
        add(traversedCandidatesLabel);
        JLabel treeDepthLabel = SlickerFactory.instance().createLabel(createDepthString());
        add(treeDepthLabel);
        JLabel lastCandidateLabel = SlickerFactory.instance().createLabel("");
        updateLastCandidateLabel(lastCandidateLabel);
        add(lastCandidateLabel);

        updateTimer = new Timer(100, e -> {
            traversedCandidatesLabel.setText(createTraversedCandidatesString());
            treeDepthLabel.setText(createDepthString());
            updateLastCandidateLabel(lastCandidateLabel);
        });
        updateTimer.start();
    }

    private void updateLastCandidateLabel(JLabel label) {
        String s = createLastCandidateString();
        label.setText(s.length() > 50 ? s.substring(0, 50) + "..." : s);
        label.setToolTipText(s);
    }

    private String createTraversedCandidatesString() {
        int count = specpp.currentStepCount();
        return "#Traversed Candidate Places: " + count + "/" + maxCandidates + String.format(" (%.2f%%)", 100 * count / maxCandidates.doubleValue());
    }

    private String createDepthString() {
        Place place = specpp.lastCandidate();
        return "#Depth of last Candidate: " + (place != null ? Integer.toString(place.size()) : "?") + "/" + maxTreeDepth;
    }

    private String createLastCandidateString() {
        Place place = specpp.lastCandidate();
        return "Last Candidate: " + (place == null ? "?" : place);
    }


    @Override
    public void destroy() {
        updateTimer.stop();
    }
}
