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

import static music.extractors.chord.ChordScanner.printRankings;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * Channel Harmony Ranking algorithm class
 * Recognizes the harmony channel
 * @author Martin Minovski <martin at minovski.net>
 */
public class ChannelRanking {

    /**
     * Detailed ranking logging
     */
    public static final boolean printRankings = false;

    /**
     * MIDI data value for NOTE ON
     */
    public static final int NOTE_ON = 0x90;

    /**
     * MIDI data value for NOTE OFF
     */
    public static final int NOTE_OFF = 0x80;
    int[] channelRanking = new int[16];
    boolean[] noteRanking = new boolean[128];

    /**
     * Creates and runs the channel ranker
     * @param sequence
     */
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

    /**
     * Returns the number of winning channel
     * @return Channel number
     */
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
