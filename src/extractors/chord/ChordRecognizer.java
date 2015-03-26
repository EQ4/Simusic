/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extractors.chord;

import elements.Chord;
import java.util.*;

/**
 *
 * @author Martin
 */
public class ChordRecognizer {

    final boolean DEBUG = false;
    int base;
    List<Integer> notes;
    String lastChord;

    public ChordRecognizer() {
        base = 128;
        notes = new ArrayList<Integer>();
        lastChord = "";
    }

    public void addNote(int note) {
        notes.add(note);
    }

    public void removeNote(int note) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).equals(note)) {
                notes.remove(i);
            }
        }
    }

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
