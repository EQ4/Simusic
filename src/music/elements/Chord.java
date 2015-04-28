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
import java.util.ArrayList;

/**
 * Chord class inheriting Playable abstract class
 * Contains a Note object defining chord base
 * The mode string can be "maj" and "min"
 * @author Martin Minovski <martin at minovski.net>
 */
public class Chord extends Playable implements Serializable {

    /**
     * Will be changed if new chord modes are added.
     */
    public static final int maxMarkovInteger = 24;
    //ArrayList<Integer> members;
    Note base;
    String mode;
    

    /**
     * A public field used by agents in chord auctions to store chord auction winner
     */
        public int agentID;

    /**
     * Temporary field. Must be removed
     */
    public boolean isMutated = false;
    
    /**
     * An empty constructor
     */
    public Chord(){
    };

    /**
     * Default chord constructor
     * @param letter The base of the chord
     * @param mode The mode of the chord - can be currently either "maj" or "min"
     */
    public Chord(String letter, String mode) {
        //members = new ArrayList<>();
        this.base = new Note(letter);
        this.mode = mode;
    }

    @Override
    public String toString() {
        return base.toString() + "-" + mode;
    }
    
    /**
     * Returns the chord mode - either "maj" or "min"
     * @return Chord mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Gets the full chord name along with its probability, if previously set
     * @return Full chord name and probability
     */
    public String getNameAndProbability() {
        if (!hasProbability()) {
            return this.toString();
        }
        return getRoundedProbability() + " - " + this.toString();
    }

    /**
     * Gets the Markov integer M of the chord.
     * M = (B % 12) + (isMajor() ? 12 : 0)
     * Where B is the base of the chord
     * If the chord is major, adds 12 to the base.
     * That way, minor chords range from 0 to 11 (inclusive)
     * And major chords - from 12 to 23 (inclusive)
     * Markov integers are used in the matrix of the Markov chain to numerate rows/columns
     * @return The Markov integer M
     */
    @Override
    public int getMarkovInteger() {
        return base.getMarkovInteger() + (this.isMajor() ? 12 : 0);
    }
    
    /**
     * Gets the maximum Markov integer (final var)
     * Markov integers are used in the matrix of the Markov chain to numerate rows/columns
     * @return Maximum Markov integer
     */
    @Override
    public int getMaximumMarkovInteger() {
        return maxMarkovInteger;
    }

    /**
     * Creates a new Chord playable from a given Markov integer
     * Markov integers are used in the matrix of the Markov chain to numerate rows/columns
     * @param numeric The Markov integer
     * @return A new Chord object returned as abstract Playable
     */
    @Override
    public Playable getNewPlayableFromMarkovNumeric(int numeric) {
        String newLetter = Note.integerToNote(numeric % 12);
        String newMode = (numeric >= 12) ? "maj" : "min";
        return new Chord(newLetter, newMode);
    }
    
    /**
     * Returns "Chord"
     * @return New string "Chord"
     */
    @Override
    public String getType() {
        return "Chord";
    }

    /**
     * Gets the Markov integer of the base note
     * Markov integers are used in the matrix of the Markov chain to numerate rows/columns
     * @return Markov integer
     */
    public int getBaseNumeric() {
        return base.getMarkovInteger();
    }

    /**
     * Returns a transposed copy of this chord 
     * @param transposeNumber The number of semitones to transpose in upward direction
     * @return A new transposed chord
     */
    public Chord getTransposedTwinChord(int transposeNumber) {
        return new Chord(new Note(Note.integerToNote(transposeNumber, base.toString())).toString(), mode);
    }

    /**
     * Is the chord major?
     * @return True if major.
     */
    public boolean isMajor() {
        if (mode.equals("maj")) {
            return true;
        }
        return false;
    }

    /**
     * Not yet implemented
     * @param member A member note to add
     */
    public void addMember(int member) {
        // Planned?
    }

    /**
     * Returns the base note letter as string
     * @return The base letter
     */
    public String getBaseLetter() {
        return base.toString();
    }
}
