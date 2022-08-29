package org.processmining.specpp.datastructures.tree.nodegen;

public class DepthLimiter implements ExpansionStopper {

    private int maxDepth;

    public DepthLimiter(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public DepthLimiter() {
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void updateToMinimum(int maxDepth) {
        this.maxDepth = Math.min(this.maxDepth, maxDepth);
    }

    @Override
    public boolean notAllowedToExpand(PlaceNode placeNode) {
        return placeNode.getDepth() >= maxDepth;
    }
}
