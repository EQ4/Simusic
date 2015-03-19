/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chordextract;

import static chordextract.ChordScanner.printRankings;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 *
 * @author Martin
 */
public class ChannelRanking {

    public static final boolean printRankings = false;
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    int[] channelRanking = new int[16];
    boolean[] noteRanking = new boolean[128];

    public ChannelRanking(Sequence sequence) {
        initializeRankings();
        
        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;

                    if ((sm.getCommand() == NOTE_ON) && (sm.getData2() != 0)) {
                        noteOnScan(sm.getData1(), sm.getChannel());
                    } else if ((sm.getCommand() == NOTE_ON) || (sm.getCommand() == NOTE_OFF)) {
                        noteOffScan(sm.getData1(), sm.getChannel());
                    }
                }
            }
        }

        if (printRankings) System.out.println("Channel Rankings:\n\n" + getRankingResults());
        
    }

    private void noteOnScan(int note, int channel) {
        noteRanking[note] = true;
        for (int i = 0; i < 128; i++) {
            if (noteRanking[i]) {
                channelRanking[channel]++;
            }
        }
    }

    private void noteOffScan(int note, int channel) {
        noteRanking[note] = false;
    }

    private void initializeRankings() {
        for (int i = 0; i < 16; i++) {
            channelRanking[i] = 0;
        }
        for (int i = 0; i < 128; i++) {
            noteRanking[i] = false;
        }
    }

    private String getRankingResults() {
        String results = "";
        for (int i = 0; i < 16; i++) {
            results += "Channel " + i + ": " + channelRanking[i] + " points\n";
        }
        return results;
    }

    public int getWinningChannel() {
        int value = 0;
        int maxPoints = 0;
        for (int i = 0; i < 16; i++) {
            if (channelRanking[i] > maxPoints) {
                value = i;
                maxPoints = channelRanking[i];
            };
        }

        return value;
    }
}
