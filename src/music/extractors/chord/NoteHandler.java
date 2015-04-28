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
package music.extractors.chord;

import music.elements.Chord;
import java.util.*;

/**
 * Chord recognizer for a single file
 * Transposes the song into C Major / A Minor for consistency
 * @author Martin Minovski <martin at minovski.net>
 */
public class NoteHandler {

    final boolean DEBUG = false;
    int base;
    List<Integer> notes;
    String lastChord;

    /**
     * Default constructor
     */
    public NoteHandler() {
        base = 128;
        notes = new ArrayList<Integer>();
        lastChord = "";
    }

    /**
     * Adds note to live scanner
     * @param note
     */
    public void addNote(int note) {
        notes.add(note);
    }

    /**
     * Removes note from live scanner
     * @param note Note
     */
    public void removeNote(int note) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).equals(note)) {
                notes.remove(i);
            }
        }
    }

    /**
     * Gets the current absolute notes?
     * @return Notes
     */
    public List<Integer> getCurrentNotesAbsolute() {
        List<Integer> newNotes = new ArrayList<Integer>();
        base = 128;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i) < base) {
                base = notes.get(i);
            }
        }
        for (int i = 0; i < notes.size(); i++) {
            newNotes.add(notes.get(i) - base);
        }
        return newNotes;
    }

    /**
     *
     * @return
     */
    public List<Integer> getCurrentNotesCondensed() {
        List<Integer> newNotes = new ArrayList<Integer>();
        List<Integer> absoluteNotes = getCurrentNotesAbsolute();
        for (int i = 0; i < absoluteNotes.size(); i++) {
            int noteToAdd = absoluteNotes.get(i) % 12;
            if (!newNotes.contains(noteToAdd)) {
                newNotes.add(noteToAdd);
            }
        }
        return newNotes;
    }

    /**
     * Prints note list
     * @param list
     */
    public void printNotes(List list) {
        if (list.isEmpty()) {
            if (DEBUG) {
                System.out.print("Nothing, ");
            }
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i) + ", ");
        }

        //System.out.println();
    }

    /**
     * Employs the chord DB
     * @param sequence
     */
    public void actionMethod(ArrayList<Chord> sequence) {

        List<Integer> workingList = getCurrentNotesCondensed();
        Collections.sort(workingList);
        //printNotes(workingList);


        ChordDatabase chordDB = new ChordDatabase();
        String chordString = chordDB.getChord(workingList, base);

        if (!chordString.isEmpty()) {
            if (!lastChord.equals(chordString)) {
                sequence.add(new Chord(chordString.split("-")[0], chordString.split("-")[1]));
            }
            lastChord = chordString;
        }
    }
}
