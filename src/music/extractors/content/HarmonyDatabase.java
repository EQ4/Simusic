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
package music.extractors.content;

import java.util.*;
import music.elements.Note;

/**
 * Database of chord modes and inversions
 * Currently only major and minor chords implemented, but will add more in future
 * @author Martin Minovski <martin at minovski.net>
 */
public class HarmonyDatabase {

    int[][] dbNotes = {
        
        /* 
        TODO: Uncomment in future
        
        {0, 3, 6, 9}, //dim7

        {0, 4, 7, 10}, //dom7 #1
        {0, 3, 6, 8}, //dom7 #2
        {0, 3, 5, 9}, //dom7 #3
        {0, 2, 6, 9}, //dom7 #4

        {0, 4, 7, 11}, //maj7 #1
        {0, 3, 7, 8}, //maj7 #2
        {0, 4, 5, 9}, //maj7 #3
        {0, 1, 5, 8}, //maj7 #4

        {0, 3, 7, 10}, //min7 or maj6 #1
        {0, 4, 7, 9}, //min7 or maj6 #2
        {0, 3, 5, 8}, //min7 or maj6 #3
        {0, 2, 5, 9}, //min7 or maj6 #4

        {0, 3, 7, 9}, //min6 #1
        {0, 4, 6, 9}, //min6 #2
        {0, 2, 5, 8}, //min6 #3
        {0, 3, 6, 10}, //min6 #4

        {0, 4, 8}, //aug

        {0, 3, 6}, //dim #1
        {0, 3, 9}, //dim #2
        {0, 6, 6}, //dim #3
        
        {0, 5, 7}, //sus4 (or 2) #1
        {0, 2, 7}, //sus4 (or 2) #2
        {0, 5, 10}, // sus4 (or 2) #3
        

        */
        
        // The only used modes currently:
        
        {0, 4, 7}, //maj #1
        {0, 3, 8}, //maj #2
        {0, 5, 9}, //maj #3

        {0, 3, 7}, //min #1
        {0, 4, 9}, //min #2
        {0, 5, 8}, //min #3

    };
    String[][] dbNames = {
        
        /*
        {"dim7", "1"},//

        {"dom7", "1"},//
        {"dom7", "2"},//
        {"dom7", "3"},//
        {"dom7", "4"},//

        {"maj7", "1"},//
        {"maj7", "2"},//
        {"maj7", "3"},//
        {"maj7", "4"},//

        {"min7", "1"},//
        {"min7", "2"},//
        {"min7", "3"},//
        {"min7", "4"},//

        {"min6", "1"},//
        {"min6", "2"},//
        {"min6", "3"},//
        {"min6", "4"},//

        {"aug", "1"},//

        {"dim", "1"},//
        {"dim", "2"},//
        {"dim", "3"},//

        {"sus4", "1"},//
        {"sus4", "2"},//
        {"sus4", "3"},//
        
        */
        
        {"maj", "1"},//
        {"maj", "2"},//
        {"maj", "3"},//

        {"min", "1"},//
        {"min", "2"},//
        {"min", "3"},//

    };
    int[] dbOffset = {
        /*
        0,
        0,//
        8,//
        5,//
        2,//

        0,//
        8,//
        5,//
        1,//

        0,//
        9,//
        5,//
        2,//

        0,//
        9,//
        5,//
        3,//

        0,//

        0,//
        9,//
        6,//

        
        0,//
        7,//
        5,//
        
        
        */
        0,//maj
        8,//
        5,//

        0,//
        9,//
        5,//

    };

    /**
     * Default constructor; empty.
     */
    public HarmonyDatabase() {
    }

    /**
     * Chord recognition method
     * If a chord has been recognized, return the chord
     * If not, return an empty string
     * @param notes The list of MIDI pitch values of notes to recognize
     * @param base The lowest note (base not in musical sense, more like bass)
     * @return Recognized chord as string, "" if none
     */
    public String getChord(List<Integer> notes, int base) {
        String chord = "";

        for (int i = 0; i < dbNotes.length; i++) {
            boolean success = true;
            for (int j = 0; j < dbNotes[i].length; j++) {
                if (!notes.contains(dbNotes[i][j])) {
                    success = false;
                }
            }
            if (success) {
                chord = Note.integerToNoteString(base + dbOffset[i]) + "-" + dbNames[i][0];
                break;
            }
        }

        return chord;
    }
}
