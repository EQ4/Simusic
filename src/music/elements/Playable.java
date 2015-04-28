/*
 * The MIT License
 *
 * Copyright 2015 Martin Minovski <martin at minovski.net>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package music.elements;

import java.io.Serializable;
import run.Main;

/**
 * Abstract class Playable
 * By design, the Simusic MIDI Player should be able to play an object of type Playable
 * Also stores the probability values used by agents
 * @author Martin Minovski <martin at minovski.net>
 */
public abstract class Playable implements Comparable, Serializable {

    static final int PROBABILITY_COMPARE_MULTIPLY_FACTOR = 10000;

    private double probability = -1;
    private boolean hasProbability = false;

    private int count = 0;
    private int total = 0;

    /**
     * Sets the playable probability value
     * @param probability The new probability value
     */
    public void setProbability(double probability) {
        this.probability = probability;
        this.hasProbability = true;
    }

    /**
     * Checks if playable has probability
     * @return True if probability is set
     */
    public boolean hasProbability() {
        return hasProbability;
    }

    /**
     * Gets a the probability of the playable
     * Also used as utility value by agents
     * @return A Double object is probability is set, null if not.
     */
    public Double getProbability() {
        if (!hasProbability()) {
            // Prevents non-probability playables from returning a value
            return null;
        }
        return probability;
    }

    /**
     * Increases the playable probability by an increment
     * @param increment The amount to increase by
     */
    public void increaseProbability(double increment) {
        this.probability += increment;
    }

    /**
     * Sets the count of the playable in the Markov chain matrix
     * @param count Playable count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Gets the count of the playable in the Markov chain matrix
     * @return Playable count
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the total of the whole row in the Markov chain matrix
     * Used to calculate probability
     * @param total Row total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Gets the total of the whole row in the Markov chain matrix
     * Used to calculate probability
     * @return Row total
     */
    public int getTotal() {
        return total;
    }

    /**
     * Returns playable's Markov integer. Must be overriden
     * @return Markov integer
     */
    public abstract int getMarkovInteger();

    /**
     * Returns the maximum Markov integer. Overriden
     * @return Maximum Markov Integer
     */
    public abstract int getMaximumMarkovInteger();

    /**
     * Overriden
     * @param numeric Overriden
     * @return Overriden
     */
    public abstract Playable getNewPlayableFromMarkovNumeric(int numeric);

    /**
     * Returns playable type string. Overriden
     * @return Type string
     */
    public abstract String getType();

    @Override
    public abstract String toString();

    @Override
    public int compareTo(Object comparePlayable) {
        Playable compareCast = (Playable) comparePlayable;
        double compareProbability = compareCast.getProbability();
        return ((int) (compareProbability * PROBABILITY_COMPARE_MULTIPLY_FACTOR)) - ((int) (this.probability * PROBABILITY_COMPARE_MULTIPLY_FACTOR));
    }

    /**
     * Gets rounded probability value (3 digits)
     * @return Rounded probability
     */
    public Double getRoundedProbability() {
        return Main.getRoundedValue(getProbability());
    }
}
