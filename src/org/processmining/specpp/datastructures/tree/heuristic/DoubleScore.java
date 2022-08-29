package org.processmining.specpp.datastructures.tree.heuristic;

public class DoubleScore implements HeuristicValue<DoubleScore> {

    private final double score;

    public DoubleScore(double score) {
        this.score = score;
    }


    public double getScore() {
        return score;
    }

    @Override
    public int compareTo(DoubleScore o) {
        return Double.compare(score, o.score);
    }

    @Override
    public String toString() {
        return Double.toString(score);
    }

}
