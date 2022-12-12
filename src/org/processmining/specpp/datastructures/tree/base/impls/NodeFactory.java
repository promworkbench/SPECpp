package org.processmining.specpp.datastructures.tree.base.impls;

import org.processmining.specpp.config.components.SimpleBuilder;
import org.processmining.specpp.datastructures.graph.Annotatable;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.KnowsParent;
import org.processmining.specpp.datastructures.tree.base.traits.MutableChildren;
import org.processmining.specpp.datastructures.tree.base.traits.MutableParent;
import org.processmining.specpp.util.Reflection;

public class NodeFactory<N extends TreeNode> {

    private final Class<N> nodeClass;

    public NodeFactory(Class<N> nodeClass) {
        this.nodeClass = nodeClass;
    }

    public N createNode(Object... args) {
        return Reflection.instance(nodeClass, args);
    }


    @SuppressWarnings("unchecked")
    public N childOf(N parent) {
        N instance = parent instanceof KnowsParent ? createNode(parent) : createNode();
        if (parent instanceof MutableChildren) ((MutableChildren<N>) parent).addChild(instance);
        return instance;
    }

    public InProgress newChildOf(N parent) {
        N instance = createNode(parent);
        if (parent instanceof MutableChildren) {
            try {
                MutableChildren<N> mutableChildren = (MutableChildren<N>) parent;
                mutableChildren.addChild(instance);
            } catch (ClassCastException ignored) {
            }
        }
        return new InProgress(instance);
    }

    public InProgress newNode() {
        return new InProgress(createNode());
    }


    @SuppressWarnings("unchecked")
    public class InProgress implements SimpleBuilder<N> {

        private final N createdNode;

        public InProgress(N createdNode) {
            this.createdNode = createdNode;
        }


        public <A> InProgress annotate(A annotation) {
            if (createdNode instanceof Annotatable) {
                try {
                    Annotatable<A> annotatable = (Annotatable<A>) createdNode;
                    annotatable.setAnnotation(annotation);
                } catch (ClassCastException ignored) {
                }
            }
            return this;
        }

        public InProgress addChild(N child) {
            if (createdNode instanceof MutableChildren) {
                try {
                    MutableChildren<N> node = (MutableChildren<N>) createdNode;
                    node.addChild(child);
                } catch (ClassCastException ignored) {
                }
            }
            return this;
        }

        public InProgress setParent(N parent) {
            if (createdNode instanceof MutableParent) {
                try {
                    MutableParent<N> node = (MutableParent<N>) createdNode;
                    node.setParent(parent);
                } catch (ClassCastException ignored) {
                }
            }
            return this;
        }

        @Override
        public N build() {
            return createdNode;
        }
    }

}
