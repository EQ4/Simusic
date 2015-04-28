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
package music.player;

import java.util.Random;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import music.elements.Chord;
import run.Main;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class Player {

    Random rand;
    final Synthesizer synth;
    final MidiChannel[] channels;

    int[] instrumentToChannelMap;
    int channelCounter;

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Player player = new Player();
        player.playArpeggio(new Chord("C", "maj"), 0, 1600, 4, 70);
    }

    /**
     *
     */
    public Player() {
        rand = new Random();
        synth = getSynth();
        channels = synth.getChannels();
        openSynth(synth);
        instrumentToChannelMap = new int[127];
        for (int i = 0; i < 127; i++) {
            instrumentToChannelMap[i] = -1;
        }
        channelCounter = 0;
    }

    private Synthesizer getSynth() {
        try {
            return MidiSystem.getSynthesizer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openSynth(Synthesizer synth) {
        try {
            synth.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param chord
     * @param instrument
     * @param measureTime
     * @param notesToPlay
     * @param velocity
     */
    public void playArpeggio(Chord chord, int instrument, int measureTime, int notesToPlay, int velocity) {
        TonePicker tonePicker = new TonePicker(chord.isMajor(), rand);
        int channel = instrumentToChannelMap[instrument];
        if (channel == -1) {
            //Max 9 instruments
            if (channelCounter < 9) {
                instrumentToChannelMap[instrument] = channelCounter;
                channel = channelCounter;
                //Change MIDI program
                channels[channelCounter].programChange(instrument);
                channelCounter++;
            } else {
                return;
            }
        }

        //Now play
        int base = chord.getMarkovInteger() % 12;

        for (int i = 0; i < notesToPlay; i++) {
            int bassNote = 36 + base;
            if (i == 0) {
                channels[channel].noteOn(bassNote, velocity);
            }
            
            
            
            int noteNumber = 48 + (rand.nextInt(2) * 12) + base + tonePicker.getNextTone();
            channels[channel].noteOn(noteNumber, velocity);
            Main.wait(measureTime / notesToPlay);
            channels[channel].noteOff(noteNumber);

            if (i == notesToPlay - 1) {
                channels[channel].noteOff(bassNote);
            }
        }
    }
}
