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

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import static run.Main.MAX_MIDI_PITCH;
import static run.Main.NOTE_OFF;
import static run.Main.NOTE_ON;

/**
 * Channel Harmony Ranking algorithm class Recognizes the harmony channel
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class ChannelRanking {

    //Todo: make changeable from test classes
    boolean printRankings = false;

    int[] channelHarmonyPoints = new int[16];
    int[] channelNoteOns = new int[16];
    boolean[][] notesCurrentlyOn = new boolean[16][MAX_MIDI_PITCH];

    int winningHarmonyChannel;
    int winningMelodyChannel;

    /**
     * Creates and runs the channel ranker
     *
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
                    // IMPORTANT
                    // Channel 9 or in practice 10 (drums & percussion) shouldn't be ranked. Drums have no corresponding NOTE_OFF message
                    if ((sm.getCommand() == NOTE_ON) && (sm.getData2() != 0) && (sm.getChannel() != 9)) {
                        noteOn(sm.getData1(), sm.getChannel());
                    } else if ((sm.getCommand() == NOTE_ON) || (sm.getCommand() == NOTE_OFF)) {
                        noteOff(sm.getData1(), sm.getChannel());
                    }
                }
            }
        }

        calculateWinningHarmonyChannel();
        calculateWinningMelodyChannel();
        if (printRankings) {
            System.out.println("Channel Rankings:\n\n" + getHarmonyRankingResults());
        }
    }

    private void initializeRankings() {
        //Initialize channel vars
        for (int i = 0; i < 16; i++) {
            channelHarmonyPoints[i] = 0;
            channelNoteOns[i] = 0;
            //Initialize note booleans for all channels
            notesCurrentlyOn[i] = new boolean[MAX_MIDI_PITCH];
            for (int j = 0; j < MAX_MIDI_PITCH; j++) {
                notesCurrentlyOn[i][j] = false;
            }

        }
    }

    private void noteOn(int note, int channel) {
        notesCurrentlyOn[channel][note] = true;
        for (int i = 0; i < MAX_MIDI_PITCH; i++) {
            if (notesCurrentlyOn[channel][i]) {
                channelHarmonyPoints[channel]++;
            }
        }
        channelNoteOns[channel]++;
    }

    private void noteOff(int note, int channel) {
        notesCurrentlyOn[channel][note] = false;
    }

    private String getHarmonyRankingResults() {
        String results = "";
        for (int i = 0; i < 16; i++) {
            results += "Channel " + i + ": " + channelHarmonyPoints[i] + " points\n";
        }
        return results;
    }

    /**
     * Calculates the number of winning harmony channel
     */
    public final void calculateWinningHarmonyChannel() {
        int channelNumber = 0;
        int maxPoints = Integer.MIN_VALUE;
        for (int i = 0; i < 16; i++) {
            if (channelHarmonyPoints[i] > maxPoints) {
                channelNumber = i;
                maxPoints = channelHarmonyPoints[i];
            }
        }

        winningHarmonyChannel = channelNumber;
    }

    /**
     * Calculates the number of winning melody channel
     */
    public final void calculateWinningMelodyChannel() {
        int channelNumber = 0;
        int maxPoints = Integer.MIN_VALUE;
        for (int i = 0; i < 16; i++) {
            if (channelHarmonyPoints[i] > maxPoints) {
                
                // Beta version melody ranking algorithm - to be tested!
                // For now works pretty well
                maxPoints = channelNoteOns[i] - channelHarmonyPoints[i];
                if (maxPoints == 0) {
                    continue;
                }
                channelNumber = i;
            }
        }

        winningMelodyChannel = channelNumber;
    }

    /**
     * Returns the number of winning harmony channel
     *
     * @return Channel number
     */
    public int getWinningHarmonyChannel() {
        return winningHarmonyChannel;
    }

    /**
     * Returns the number of winning melody channel
     *
     * @return Channel number
     */
    public int getWinningMelodyChannel() {
        return winningMelodyChannel;
    }
}
