package org.processmining.specpp.supervision.piping;

import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.specpp.datastructures.tree.base.AnnotatableBiDiNode;
import org.processmining.specpp.datastructures.tree.base.BiDiTree;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.supervision.observations.Visualization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TreeDrawer extends AbstractAsyncAwareObservable<Visualization<DotPanel>> implements AsyncAdHocObservable<Visualization<DotPanel>> {

    private final BiDiTree<? extends AnnotatableBiDiNode<?, ?>> tree;
    private final int fromLevel;
    private final int toLevel;
    private final int drawNodeLimit;
    private final String title;


    public TreeDrawer(BiDiTree<? extends AnnotatableBiDiNode<?, ?>> tree, int fromLevel, int toLevel, int drawNodeLimit, String title) {
        this.tree = tree;
        this.fromLevel = fromLevel;
        this.toLevel = toLevel < 0 ? Integer.MAX_VALUE : toLevel;
        this.drawNodeLimit = drawNodeLimit < 0 ? Integer.MAX_VALUE : drawNodeLimit;
        this.title = title;
    }

    @Override
    public Visualization<DotPanel> computeObservation() {
        Dot d = new Dot();
        d.setLabel(title);
        if (tree.getRoot() != null) {

            d.setDirection(Dot.GraphDirection.topDown);
            Map<AnnotatableBiDiNode<?, ?>, DotNode> map = new HashMap<>();
            Iterator<? extends IndexedItem<? extends AnnotatableBiDiNode<?, ?>>> levelwiseIterator = tree.traverseLevelwise();
            int count = 0;
            while (levelwiseIterator.hasNext()) {
                IndexedItem<? extends AnnotatableBiDiNode<?, ?>> next = levelwiseIterator.next();
                int level = next.getIndex();
                if (level < fromLevel) continue;
                if (level > toLevel) break;
                if (count >= drawNodeLimit) break;
                AnnotatableBiDiNode<?, ?> vertex = next.getItem();
                DotNode dotNode = d.addNode(vertex.toString());
                map.put(vertex, dotNode);
                count++;
            }

            for (Map.Entry<AnnotatableBiDiNode<?, ?>, DotNode> entry : map.entrySet()) {
                AnnotatableBiDiNode<?, ?> vertex = entry.getKey();
                DotNode dotNode = entry.getValue();
                for (AnnotatableBiDiNode<?, ?> child : vertex.getChildren()) {
                    if (map.containsKey(child)) d.addEdge(dotNode, map.get(child));
                }
            }
        }

        DotPanel panel = new DotPanel(d);

        return new Visualization<>(title, panel);
    }

}
