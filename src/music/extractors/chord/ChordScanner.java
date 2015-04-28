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

import music.extractors.chord.NoteHandler;
import music.elements.Chord;
import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * Helper chord scanner class
 * @author Martin Minovski <martin at minovski.net>
 */
public class ChordScanner {

    /**
     * Logs rankings
     */
    public static final boolean printRankings = false;

    /**
     * If false, the notes of all channels will be used as harmony
     * Do not change
     */
    public static final boolean useSingleChannel = true;

    /**
     * MIDI Note On Value
     */
    public static final int NOTE_ON = 0x90;

    /**
     * MIDI Note Off Value
     */
    public static final int NOTE_OFF = 0x80;

    /**
     * The ChordRecognizer Instance
     */
    public NoteHandler recognizer = new NoteHandler();
    ArrayList<Chord> chordSequence;
    private int channelNumber = 0;

    private void noteOn(int note, int channel) {
        if ((channel == channelNumber) || (!useSingleChannel)) {
            recognizer.addNote(note);
            recognizer.actionMethod(chordSequence);
        }
    }

    private void noteOff(int note, int channel) {
        if ((channel == channelNumber) || (!useSingleChannel)) {
            recognizer.removeNote(note);
            recognizer.actionMethod(chordSequence);
        }
    }

    /**
     * Default constructor - scans a single file
     * @param path The file to scan
     * @throws Exception In every life we have some trouble, and if you worry you make it double. Don't worry. Be happy.
     */
    public ChordScanner(String path) throws Exception {

        chordSequence = new ArrayList<>();

        Sequence sequence = MidiSystem.getSequence(new File(path));

        ChannelRanking ranking = new ChannelRanking(sequence);
        channelNumber = ranking.getWinningChannel();


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

    /**
     * Returns the chord sequence of the song
     * @return
     */
    public ArrayList<Chord> getSequence() {
        return chordSequence;
    }
    
    public int getWinningChannel() {
        return channelNumber;
    }
}
