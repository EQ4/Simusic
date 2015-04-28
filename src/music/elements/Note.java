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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package music.elements;

import java.io.Serializable;

/**
 * Note class inheriting Playable abstract class
 * @author Martin Minovski <martin at minovski.net>
 */
public class Note extends Playable implements Serializable {

    /**
     * Only 12 semitones per octave, hence the Markov integer
     * This field is final
     * Markov integers are used in the matrix of the Markov chain to numerate rows/columns
     */
    public static final int maxMarkovInteger = 12;

    /**
     * Note letter array
     * From C (0) to B (11)
     * MIDI compatible
     */
    public static final String letters[] = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private String letter;

    /**
     * Adds semitones to a note and returns the new note string
     * @param integer Number of semitones to add
     * @param key The original note
     * @return The new note
     */
    public static String integerToNote(int integer, String key) {
        int keyInt = 0;
        for (int i = 0; i < 12; i++) {
            if (key.equals(Note.letters[i])) {
                keyInt = i;
            }
        }
        return Note.letters[(keyInt + integer) % 12];
    }

    /**
     * Gets a note string representation of a pitch value
     * E.g. if 2 is passed as the argument, returns "D"
     * @param integer Note integer
     * @return The note string
     */
    public static String integerToNoteString(int integer) {
        return integerToNote(integer, "C");
    }
    
    
    /**
     * Gets a new note object from pitch value
     * E.g. if 2 is passed as the argument, returns a new D note
     * @param integer Note integer
     * @return The note object
     */
    public static Note integerToNewNote(int integer) {
        return new Note(integerToNoteString(integer));
    }

    /**
     * An empty constructor
     */
    public Note(){
        //Empty constructor
    };
    
    /**
     * Default constructor
     * @param letter The note letter
     */
    public Note(String letter) {
        this.letter = letter;
    }

    /**
     * Gets the Markov integer of the note - same as the note integer representation
     * If note is C, integer is 0. Increments analogically.
     * Markov integers are used in the matrix of the Markov chain to numerate rows/columns
     * @return A Markov integer
     */
    @Override
    public int getMarkovInteger() {
        return getIntegerRepresentation();
    }
    
    /**
     * Returns the maximum Markov integer possible for a note
     * Markov integers are used in the matrix of the Markov chain to numerate rows/columns
     * @return
     */
    @Override
    public int getMaximumMarkovInteger() {
        return maxMarkovInteger;
    }

    /**
     * Creates and returns a new note with pitch specified in argument
     * @param numeric The pitch value 0-12 (exclusive)
     * @return New note as playable
     */
    @Override
    public Playable getNewPlayableFromMarkovNumeric(int numeric) {
        return new Note(Note.integerToNoteString(numeric));
    }
    
    /**
     * Returns the string "Note"
     * @return "Note"
     */
    @Override
    public String getType() {
        return "Note";
    }

    /**
     * Returns the integer representation of the note
     * If note is C, returns 0.
     * @return The integer representation
     */
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
