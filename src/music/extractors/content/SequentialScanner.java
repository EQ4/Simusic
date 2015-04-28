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

import music.elements.Chord;
import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import music.elements.Note;
import static run.Main.NOTE_OFF;
import static run.Main.NOTE_ON;

/**
 * Helper chord scanner class
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class SequentialScanner {

    ChannelRanking ranking;
    public HarmonyNoteHandler harmonyHandler = new HarmonyNoteHandler();
    ArrayList<Chord> chordSequence;
    ArrayList<Note> noteSequence;

    //Melody line variables
    private boolean melodyLineLock;
    private int lastMelodyPitchValue;

    /**
     * Default constructor - scans a single file
     *
     * @param path The file to scan
     * @throws Exception In every life we have some trouble, and if you worry
     * you make it double. Don't worry. Be happy.
     */
    public SequentialScanner(String path) throws Exception {
        Sequence sequence = MidiSystem.getSequence(new File(path));

        // Get the melody and harmony rankings
        this.ranking = new ChannelRanking(sequence);

        // Initializes content sequences
        this.chordSequence = new ArrayList<>();
        this.noteSequence = new ArrayList<>();
        this.melodyLineLock = false;

        // Main scan loop for harmony and melody
        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if ((sm.getCommand() == NOTE_ON) && (sm.getData2() != 0)) {
                        noteOn(sm.getData1(), sm.getChannel());
                    } else if ((sm.getCommand() == NOTE_ON) || (sm.getCommand() == NOTE_OFF)) {
                        noteOff(sm.getData1(), sm.getChannel());
                    }
                }
            }
        }
    }

    private void noteOn(int note, int channel) {
        if (channel == ranking.getWinningHarmonyChannel()) {
            harmonyHandler.addNote(note);
            harmonyHandler.harmonyAction(chordSequence);
        }
        if (channel == ranking.getWinningMelodyChannel()) {
            //If melody lock is free, obtain it
            //Also note should be different from last melody note
            if ((!melodyLineLock) && (note % 12 != lastMelodyPitchValue % 12)) {
                melodyLineLock = true;
                lastMelodyPitchValue = note;
                noteSequence.add(Note.integerToNewNote(note));
            }
        }
    }

    private void noteOff(int note, int channel) {
        if (channel == ranking.getWinningHarmonyChannel()) {
            harmonyHandler.removeNote(note);
            harmonyHandler.harmonyAction(chordSequence);
        }
        if (channel == ranking.getWinningMelodyChannel()) {
            if ((melodyLineLock) && (note == lastMelodyPitchValue)) {
                // If the unplayed note equals the last melody note, release melody lock
                melodyLineLock = false;
            }
        }
    }

    /**
     * Returns the chord sequence of the song
     *
     * @return
     */
    public ArrayList<Chord> getHarmonySequence() {
        return chordSequence;
    }

    public ArrayList<Note> getMelodySequence() {
        return noteSequence;
    }

    public int getWinningHarmonyChannel() {
        return ranking.getWinningHarmonyChannel();
    }

    public int getWinningMelodyChannel() {
        return ranking.getWinningMelodyChannel();
    }
}
