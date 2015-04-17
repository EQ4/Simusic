/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package music.elements;

import java.util.ArrayList;

/**
 *
 * @author Martin
 */
public class Chord extends Playable {

    public static final int maxMarkovInteger = 24;
    ArrayList<Integer> members;
    Note base;
    String mode;
    
    public Chord(){
        //Empty constructor
    };

    public Chord(String letter, String mode) {
        members = new ArrayList<>();
        this.base = new Note(letter);
        this.mode = mode;
    }

    @Override
    public String toString() {
        return base.toString() + "-" + mode;
    }

    public String getNameAndProbability() {
        if (!hasProbability()) {
            return this.toString();
        }
        return getRoundedProbability() + " - " + this.toString();
    }

    @Override
    public int getMarkovInteger() {
        return base.getMarkovInteger() + (this.isMajor() ? 12 : 0);
    }
    
    @Override
    public int getMaximumMarkovInteger() {
        return maxMarkovInteger;
    }

    @Override
    public Playable getNewPlayableFromMarkovNumeric(int numeric) {
        String newLetter = Note.integerToNote(numeric % 12);
        String newMode = (numeric >= 12) ? "maj" : "min";
        return new Chord(newLetter, newMode);
    }
    
    @Override
    public String getType() {
        return "Chord";
    }

    public int getBaseNumeric() {
        return base.getMarkovInteger();
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
}
