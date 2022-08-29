package org.processmining.specpp.datastructures.tree.base.impls;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.processmining.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.specpp.datastructures.tree.base.PropertyNode;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.events.NodeExhaustionEvent;
import org.processmining.specpp.datastructures.tree.events.NodeExpansionEvent;
import org.processmining.specpp.datastructures.tree.events.TreeNodeEvent;
import org.processmining.specpp.supervision.piping.Observer;
import org.processmining.specpp.util.JavaTypingUtils;

public class ChangeTree<P extends NodeProperties> extends BiDiTreeImpl<AnnotatableBiDiNodeImpl<String>> implements Observer<TreeNodeEvent<PropertyNode<P>>> {

    private final BidiMap<TreeNode, AnnotatableBiDiNodeImpl<String>> map;
    private final static NodeFactory<AnnotatableBiDiNodeImpl<String>> nodeFactory = new NodeFactory<>(JavaTypingUtils.castClass(AnnotatableBiDiNodeImpl.class));

    public ChangeTree() {
        super();
        map = new DualHashBidiMap<>();
    }

    @Override
    public void observe(TreeNodeEvent<PropertyNode<P>> event) {
        PropertyNode<P> source = event.getSource();
        synchronized (map) {
            if (event instanceof NodeExpansionEvent) {
                if (!map.containsKey(source)) {
                    AnnotatableBiDiNodeImpl<String> r = nodeFactory.newNode()
                                                                   .annotate(source.getProperties().toString())
                                                                   .build();
                    setRoot(r);
                    map.put(source, r);
                }
                AnnotatableBiDiNodeImpl<String> affectedNode = map.get(source);
                NodeExpansionEvent<PropertyNode<P>> expansionEvent = (NodeExpansionEvent<PropertyNode<P>>) event;
                PropertyNode<P> child = expansionEvent.getChild();
                AnnotatableBiDiNodeImpl<String> corrChild = nodeFactory.newChildOf(affectedNode)
                                                                       .annotate(child.getProperties().toString())
                                                                       .build();
                map.put(child, corrChild);
            } else if (event instanceof NodeExhaustionEvent) {
                if (map.containsKey(source)) {
                    AnnotatableBiDiNodeImpl<String> affectedNode = map.get(source);
                    nodeFactory.newChildOf(affectedNode).annotate("X").build();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ChangeTree:" + super.toString();
    }
}
