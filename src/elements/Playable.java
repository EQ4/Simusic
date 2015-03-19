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

    public static Playable getPlayableFromMarkovNumeric(int numeric, Type type) {
        switch (type) {
            case CHORD:
                String letter = Note.integerToNote(numeric % 12);
                String mode = (numeric >= 12) ? "maj" : "min";
                return new Chord(letter, mode);
            case NOTE:
                return new Note(Note.integerToNote(numeric));
            default:
                break;
        }
        return null;
    }
    static final int probabilityCompareMultiplyFactor = 10000;
    static final int probabilityRoundValue = 1000;
    double probability;
    boolean hasProbability;

    public Playable() {
        this.probability = -1;
        this.hasProbability = false;
    }

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

    public abstract int getMarkovInteger();

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
