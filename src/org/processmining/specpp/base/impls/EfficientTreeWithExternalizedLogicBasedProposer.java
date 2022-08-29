package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.specpp.datastructures.tree.base.ChildGenerationLogic;
import org.processmining.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.specpp.datastructures.tree.base.NodeState;
import org.processmining.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;

public abstract class EfficientTreeWithExternalizedLogicBasedProposer<C extends Candidate & NodeProperties, S extends NodeState, N extends LocalNodeWithExternalizedLogic<C, S, N>> extends AbstractEfficientTreeBasedProposer<C, N> {

    protected final ChildGenerationLogic<C, S, N> generationLogic;

    public EfficientTreeWithExternalizedLogicBasedProposer(ChildGenerationLogicComponent<C, S, N> generationLogic, EfficientTreeComponent<N> tree) {
        super(tree);
        this.generationLogic = generationLogic;
        registerSubComponent(generationLogic);
    }

    public ChildGenerationLogic<C, S, N> getGenerationLogic() {
        return generationLogic;
    }
}
