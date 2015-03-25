/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elements;

/**
 *
 * @author Martin
 */
public class Note extends Playable {
    
    public static final int maxMarkovInteger = 12;
    public static final String letters[] = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",};
    
    public static String integerToNote(int integer, String key) {
        int keyInt = 0;
        for (int i = 0; i < 12; i++) {
            if (key.equals(Note.letters[i])) {
                keyInt = i;
            }
        }
        return Note.letters[(keyInt + integer) % 12];
    }

    public static String integerToNote(int integer) {
        return integerToNote(integer, "C");
    }
    
    
    private String letter;
    
    public Note(String letter) {
        this.letter = letter;
    }
    
    @Override
    public int getMarkovInteger() {
        return getIntegerRepresentation();
    }
    
    public int getIntegerRepresentation() {
        int result = 0;
        for (int i = 0; i < 12; i++) {
            if (letter.equals(Note.letters[i])) {
                result = i;
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return letter;
    }
}
