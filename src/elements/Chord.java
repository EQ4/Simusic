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
public class Chord {

    public static Chord getChordFromMarkovNumeric(int numeric) {
        String letter = Note.integerToNote(numeric % 12);
        String mode = (numeric >= 12) ? "maj" : "min";
        return new Chord(letter, mode);
    }
    
    ArrayList<Integer> members;
    Note base;
    String mode;

    public Chord(String letter, String mode) {
        members = new ArrayList<>();
        this.base = new Note(letter);
        this.mode = mode;
    }

    public String getFullName() {
        return base.toString() + "-" + mode;
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
}
