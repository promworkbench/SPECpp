package org.processmining.specpp.datastructures.tree.heuristic;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.specpp.datastructures.tree.events.*;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.piping.AsyncAdHocObservableWrapper;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.JavaTypingUtils;

public class EventingHeuristicTreeExpansion<N extends TreeNode & Evaluable & LocallyExpandable<N>, H extends HeuristicValue<H>> extends HeuristicTreeExpansion<N, H> {

    private final EventSupervision<TreeHeuristicsEvent> eventSupervision = PipeWorks.eventSupervision();


    public EventingHeuristicTreeExpansion(HeuristicStrategy<N, H> heuristicStrategy) {
        super(heuristicStrategy);
        globalComponentSystem().provide(SupervisionRequirements.observable("heuristics.events", JavaTypingUtils.castClass(HeuristicComputationEvent.class), eventSupervision))
                               .provide(SupervisionRequirements.adHocObservable("heuristics.stats", HeuristicStatsEvent.class, AsyncAdHocObservableWrapper.wrap(() -> new HeuristicStatsEvent(priorityQueue.size()))));
    }

    @Override
    protected void putHeuristic(N node, H heuristic) {
        super.putHeuristic(node, heuristic);
        eventSupervision.observe(new HeuristicComputationEvent<>(node, heuristic));
    }

    @Override
    protected void enqueue(N node) {
        super.enqueue(node);
        eventSupervision.observe(new EnqueueNodeEvent<>(node));
    }

    @Override
    protected void dequeue(N node) {
        super.dequeue(node);
        eventSupervision.observe(new DequeueNodeEvent<>(node));
    }

    @Override
    protected N dequeueFirst() {
        N n = super.dequeueFirst();
        eventSupervision.observe(new DequeueNodeEvent<>(n));
        return n;
    }


}
