/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package music.elements;

import java.io.Serializable;
import run.Main;

/**
 *
 * @author Martin
 */
public abstract class Playable implements Comparable, Serializable {

    public static enum Type {

        CHORD, NOTE
    }

    static final int PROBABILITY_COMPARE_MULTIPLY_FACTOR = 10000;

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

    public void increaseProbability(double increment) {
        this.probability += increment;
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
        return ((int) (compareProbability * PROBABILITY_COMPARE_MULTIPLY_FACTOR)) - ((int) (this.probability * PROBABILITY_COMPARE_MULTIPLY_FACTOR));
    }

    public Double getRoundedProbability() {
        return Main.getRoundedValue(getProbability());
    }
}
