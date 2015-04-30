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
import javax.sound.midi.Synthesizer;
import music.elements.Chord;
import music.elements.Note;
import music.elements.Playable;
import music.elements.Sequence;
import music.extractors.feature.FeatureExtractor;
import music.extractors.feature.GlobalFeatureContainer;
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
        //Player player = new Player();
        //player.playArpeggio(new Chord("C", "maj"), 0, 1600, 4, 70);
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

    public void playHarmony(Chord chord, int instrument, FeatureExtractor agentFextract, GlobalFeatureContainer globalFeatures) {
        //Get MIDI channel number
        Integer channel = getChannelForInstrument(instrument);
        if (channel == null) {
            return;
        }

        //Initialize tone picker
        TonePicker tonePicker = new TonePicker(chord.isMajor(), rand);

        //Performance variables
        int numberOfNotesToPlay = 4; // can be 3 for TRIPLETS !!!!!!
        int beatPeriod = Main.getBeatPeriod(globalFeatures.getCurrentTempo());
        int octaveHigher = 3;
        int octavesBetweenBassAndHarmony = 1;
        int base = chord.getMarkovInteger() % 12;
        int timeOfWholeMeasure = 4 * beatPeriod;
        int bassNote = (octaveHigher * 12) + base;
        int velocity = 70;

        //Play now
        for (int i = 0; i < numberOfNotesToPlay; i++) {
            if (i == 0) {
                //First beat is always bass
                channels[channel].noteOn(bassNote, velocity);
            }
            int noteNumber = ((octaveHigher + octavesBetweenBassAndHarmony) * 12) + (rand.nextInt(2) * 12) + base + tonePicker.getNextTone();
            channels[channel].noteOn(noteNumber, velocity);
            Main.wait(timeOfWholeMeasure / numberOfNotesToPlay);
            channels[channel].noteOff(noteNumber);

            //Bass note is on for whole measure
            if (i == numberOfNotesToPlay - 1) {
                channels[channel].noteOff(bassNote);
            }
        }
    }

    public void playSoloPhrase(Sequence sequence, int instrument, FeatureExtractor agentFextract, GlobalFeatureContainer globalFeatures) {
        Integer channel = getChannelForInstrument(instrument);
        if (channel == null) {
            return;
        }

        if (sequence.isEmpty()) {
            System.out.println("Player: EMPTY SOLO SEQUENCE!");
            Main.wait(1000);
            return;
        }

        int beatPeriod = Main.getBeatPeriod(globalFeatures.getCurrentTempo());
        int timeOfWholeMeasure = 4 * beatPeriod;
        //Octave is randomized for now
        int octavesHigher = 5 + Main.rand.nextInt(2);
        int measureDividedBy = 4 + sequence.getSize();
        int velocity = 100;

        //Play now
        boolean isFirstBeat = true;
        for (Playable playable : sequence.getSequence()) {
            if (isFirstBeat) {
                velocity += 20;
                isFirstBeat = false;
            }
            Note note = (Note) playable;
            int noteNumber = note.getMarkovInteger() + (octavesHigher * 12);
            channels[channel].noteOn(noteNumber, velocity);
            Main.wait(timeOfWholeMeasure / measureDividedBy);
            channels[channel].noteOff(noteNumber);
        }
    }

    private Integer getChannelForInstrument(int instrument) {
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
                return null;
            }
        }
        return channel;
    }
}
