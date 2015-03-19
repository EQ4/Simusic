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
public class Chord extends Playable {

    
    ArrayList<Integer> members;
    Note base;
    String mode;

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
    public int getNumericRepresentation() {
        return base.getNumericRepresentation() + (this.isMajor() ? 12 : 0);
    }

    public int getBaseNumeric() {
        return base.getNumericRepresentation();
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
