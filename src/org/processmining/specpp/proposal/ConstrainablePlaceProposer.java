package org.processmining.specpp.proposal;

import org.processmining.specpp.base.ConstrainableProposer;
import org.processmining.specpp.base.Constrainer;
import org.processmining.specpp.base.impls.AbstractEfficientTreeBasedProposer;
import org.processmining.specpp.base.impls.CandidateConstraint;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.ContainerUtils;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.specpp.componenting.system.link.ProposerComponent;
import org.processmining.specpp.config.EfficientTreeConfiguration;
import org.processmining.specpp.config.SimpleBuilder;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.base.ConstrainableChildGenerationLogic;
import org.processmining.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.specpp.datastructures.tree.constraints.*;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.JavaTypingUtils;

/**
 * This is the base implementation of a <it>constrainable</it> place proposer.
 * It may receive {@code CandidateConstraint} events and in turn publishes {@code GenerationConstraint} events that may in turn be used by the {@code constrainable generator}.
 *
 * @see PlaceProposer
 * @see CandidateConstraint
 * @see ConstrainableChildGenerationLogic
 */
public class ConstrainablePlaceProposer extends AbstractBaseClass implements ConstrainableProposer<Place, CandidateConstraint<Place>>, Constrainer<GenerationConstraint>, ProposerComponent<Place> {
    protected final ChildGenerationLogicComponent<Place, PlaceState, PlaceNode> cgl;
    protected final SimpleBuilder<EfficientTreeComponent<PlaceNode>> treeBuilder;

    public static class Builder extends ComponentSystemAwareBuilder<ConstrainablePlaceProposer> {

        protected final DelegatingDataSource<EfficientTreeConfiguration<Place, PlaceState, PlaceNode>> delegatingDataSource = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(DataRequirements.efficientTreeConfiguration(), delegatingDataSource);
        }

        @Override
        protected ConstrainablePlaceProposer buildIfFullySatisfied() {
            EfficientTreeConfiguration<Place, PlaceState, PlaceNode> config = delegatingDataSource.getData();
            return new ConstrainablePlaceProposer(config.createPossiblyInstrumentedChildGenerationLogic(), config::createPossiblyInstrumentedTree);
        }


    }

    protected AbstractEfficientTreeBasedProposer<Place, PlaceNode> proposer;

    protected final EventSupervision<GenerationConstraint> constraintOutput = PipeWorks.eventSupervision();

    public ConstrainablePlaceProposer(ChildGenerationLogicComponent<Place, PlaceState, PlaceNode> cgl, SimpleBuilder<EfficientTreeComponent<PlaceNode>> treeBuilder) {
        this.cgl = cgl;
        this.treeBuilder = treeBuilder;
        globalComponentSystem().provide(SupervisionRequirements.observable("proposer.constraints", getPublishedConstraintClass(), getConstraintPublisher()))
                               .require(SupervisionRequirements.observable(SupervisionRequirements.regex("external\\.constraints.*"), getAcceptedConstraintClass()), ContainerUtils.observeResults(this));
        localComponentSystem().require(SupervisionRequirements.observable(SupervisionRequirements.regex("composer\\.constraints.*"), getAcceptedConstraintClass()), ContainerUtils.observeResults(this))
                              .require(SupervisionRequirements.observable(SupervisionRequirements.regex("composition\\.constraints.*"), getAcceptedConstraintClass()), ContainerUtils.observeResults(this))
                              .provide(SupervisionRequirements.observable("proposer.constraints", getPublishedConstraintClass(), getConstraintPublisher()));
        proposer = createSubProposer();
        setProposer(proposer);
    }

    protected AbstractEfficientTreeBasedProposer<Place, PlaceNode> createSubProposer() {
        return new PlaceProposer(cgl, treeBuilder.build());
    }

    protected void setProposer(AbstractEfficientTreeBasedProposer<Place, PlaceNode> proposer) {
        this.proposer = proposer;
        registerSubComponent(proposer);
    }

    @Override
    protected void initSelf() {
    }

    @Override
    public Place proposeCandidate() {
        return proposer.proposeCandidate();
    }

    @Override
    public void acceptConstraint(CandidateConstraint<Place> candidateConstraint) {
        PlaceNode placeNode = proposer.getPreviousProposedNode();
        if (candidateConstraint instanceof WiringConstraint) {
            constraintOutput.observe((GenerationConstraint) candidateConstraint);
        } else if (candidateConstraint instanceof ClinicallyUnderfedPlace) {
            constraintOutput.observe(new CullPostsetChildren(placeNode));
        } else if (candidateConstraint instanceof ClinicallyOverfedPlace) {
            PlaceState state = placeNode.getState();
            if (state.getPotentialPostsetExpansions().isEmpty() && !state.getPotentialPresetExpansions().isEmpty()) {
                constraintOutput.observe(new CullPresetChildren(placeNode));
            }
        }
    }

    @Override
    public Observable<GenerationConstraint> getConstraintPublisher() {
        return constraintOutput;
    }

    @Override
    public Class<GenerationConstraint> getPublishedConstraintClass() {
        return GenerationConstraint.class;
    }

    @Override
    public Class<CandidateConstraint<Place>> getAcceptedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
    }


}
