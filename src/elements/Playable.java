/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elements;

/**
 *
 * @author Martin
 */
public abstract class Playable implements Comparable {
    
    public static enum Type {

        CHORD, NOTE
    }

    
    static final int probabilityCompareMultiplyFactor = 10000;
    static final int probabilityRoundValue = 1000;
    
    private double probability = -1;
    private boolean hasProbability = false;
    
    private int count = 0;
    private int total = 0;

    public void setProbability(double probability) {
        this.probability = probability;
        this.hasProbability = true;
    }

    public boolean hasProbability() {
        return hasProbability;
    }

    public Double getProbability() {
        if (!hasProbability()) {
            // Prevents non-probability playables from returning a value
            return null;
        }
        return probability;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public int getTotal() {
        return total;
    }

    public abstract int getMarkovInteger();
    public abstract int getMaximumMarkovInteger();
    public abstract Playable getNewPlayableFromMarkovNumeric(int numeric);
    public abstract String getType();
    @Override
    public abstract String toString();

    @Override
    public int compareTo(Object comparePlayable) {
        Playable compareCast = (Playable) comparePlayable;
        double compareProbability = compareCast.getProbability();
        return ((int) (compareProbability * probabilityCompareMultiplyFactor)) - ((int) (this.probability * probabilityCompareMultiplyFactor));
    }
    

    public Double getRoundedProbability() {
        return (double) Math.round(getProbability() * probabilityRoundValue) / probabilityRoundValue;
    }
}
