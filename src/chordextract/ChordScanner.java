/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chordextract;

import chordextract.ChordRecognizer;
import elements.Chord;
import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class ChordScanner {

    public static final boolean printRankings = false;
    public static final boolean useSingleChannel = true;
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public ChordRecognizer recognizer = new ChordRecognizer();
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

    public ArrayList<Chord> getSequence() {
        return chordSequence;
    }
}
