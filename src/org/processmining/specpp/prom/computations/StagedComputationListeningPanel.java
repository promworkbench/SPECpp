package org.processmining.specpp.prom.computations;

import org.processmining.specpp.prom.mvc.discovery.ComputationListeningPanel;

import javax.swing.*;

public class StagedComputationListeningPanel<T extends OngoingStagedComputation> extends ComputationListeningPanel<OngoingStagedComputation> {
    public StagedComputationListeningPanel(String label, OngoingStagedComputation ongoingComputation) {
        super(label, ongoingComputation);
    }

    @Override
    protected void initProgress() {
        progressBar.setMinimum(0);
        progressBar.setMaximum(ongoingComputation.getStageCount());
    }

    @Override
    protected void updateProgress(ComputationEvent event) {
        if (event instanceof ComputationStageCompleted)
            SwingUtilities.invokeLater(() -> progressBar.setValue(((ComputationStageCompleted) event).getCompletedStage()));
        else if (event instanceof ComputationEnded) SwingUtilities.invokeLater(() -> stopButton.setEnabled(false));

    }
}
