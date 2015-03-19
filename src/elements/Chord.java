/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elements;

import java.util.ArrayList;

/**
 *
 * @author Martin
 */
public class Chord implements Comparable {

    static final int probabilityCompareMultiplyFactor = 10000;
    static final int probabilityRoundValue = 1000;

    public static Chord getChordFromMarkovNumeric(int numeric) {
        String letter = Note.integerToNote(numeric % 12);
        String mode = (numeric >= 12) ? "maj" : "min";
        return new Chord(letter, mode);
    }
    ArrayList<Integer> members;
    Note base;
    String mode;
    double probability;

    public Chord(String letter, String mode) {
        members = new ArrayList<>();
        this.base = new Note(letter);
        this.mode = mode;
        this.probability = -1;
    }

    public String getFullName() {
        return base.toString() + "-" + mode;
    }

    public String getNameAndProbability() {
        if (!hasProbability()) {
            return getFullName();
        }
        return getRoundedProbability() + " - " + getFullName();
    }

    public int getMarkovNumeric() {
        return base.getNumeric() + (this.isMajor() ? 12 : 0);
    }

    public int getBaseNumeric() {
        return base.getNumeric();
    }

    public Chord getTransposedTwinChord(int transposeNumber) {
        return new Chord(new Note(Note.integerToNote(transposeNumber, base.toString())).toString(), mode);
    }

    public boolean isMajor() {
        if (mode.equals("maj")) {
            return true;
        }
        return false;
    }

    public void addMember(int member) {
        // Planned?
    }

    public String getBaseLetter() {
        return base.toString();
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public boolean hasProbability() {
        return (probability != -1);
    }

    public Double getProbability() {
        if (!hasProbability()) {
            // Prevents non-probability chords from returning a value
            return null;
        }
        return probability;
    }

    public Double getRoundedProbability() {
        return (double) Math.round(getProbability() * probabilityRoundValue) / probabilityRoundValue;
    }

    @Override
    public int compareTo(Object compareChord) {
        Chord compareChordCast = (Chord) compareChord;
        double compareProbability = compareChordCast.getProbability();
        return ((int) (compareProbability * probabilityCompareMultiplyFactor)) - ((int) (this.probability * probabilityCompareMultiplyFactor));
    }
}
