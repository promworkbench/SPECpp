package org.processmining.specpp.proposal;

import org.processmining.specpp.base.impls.AbstractEfficientTreeBasedProposer;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.specpp.config.components.EfficientTreeConfiguration;
import org.processmining.specpp.config.components.SimpleBuilder;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.datastructures.util.Button;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.piping.PipeWorks;

public class RestartablePlaceProposer extends ConstrainablePlaceProposer {

    private final Button updateLocalComponentSystem = new Button();
    private final EventSupervision<ProposerSignal> proposerSignalOutput = PipeWorks.eventSupervision();
    private boolean shouldRestart;


    public static class Builder extends ConstrainablePlaceProposer.Builder {

        @Override
        protected ConstrainablePlaceProposer buildIfFullySatisfied() {
            EfficientTreeConfiguration<Place, PlaceState, PlaceNode> config = delegatingDataSource.getData();
            return new RestartablePlaceProposer(config.createPossiblyInstrumentedChildGenerationLogic(), config::createPossiblyInstrumentedTree);
        }
    }

    public RestartablePlaceProposer(ChildGenerationLogicComponent<Place, PlaceState, PlaceNode> cgl, SimpleBuilder<EfficientTreeComponent<PlaceNode>> treeBuilder) {
        super(cgl, treeBuilder);
        localComponentSystem().require(DataRequirements.dataSource("update_local_component_system", Runnable.class), updateLocalComponentSystem)
                              .provide(SupervisionRequirements.observable("proposer.signals.out", ProposerSignal.class, proposerSignalOutput))
                              .provide(SupervisionRequirements.observer("proposer.signals.in", ProposerSignal.class, this::receiveSignal));
        shouldRestart = false;
    }

    @Override
    public Place proposeCandidate() {
        if (shouldRestart) restart();
        return super.proposeCandidate();
    }

    private void receiveSignal(ProposerSignal proposerSignal) {
        if (proposerSignal instanceof RestartProposer) {
            shouldRestart = true;
        }
    }

    @Override
    protected void setProposer(AbstractEfficientTreeBasedProposer<Place, PlaceNode> proposer) {
        if (this.proposer != null) unregisterSubComponent(this.proposer);
        super.setProposer(proposer);
    }

    public void restart() {
        shouldRestart = false;
        setProposer(createSubProposer());
        updateLocalComponentSystem.press();
        proposer.init();
    }

}
