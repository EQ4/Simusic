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
package player;

import javax.sound.midi.MidiSystem;
import music.elements.Chord;
import music.extractors.feature.GlobalFeatureContainer;
import music.player.Player;
import run.Main;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class ArpeggioTest {

    public static void main(String[] args) {
        //Set MIDI synth
        try {
            Main.selectedMidiSynth = MidiSystem.getSynthesizer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Create new player
        Player player = new Player();

        //Create global feature object and set tempo
        GlobalFeatureContainer globalFeatures = new GlobalFeatureContainer();
        globalFeatures.setCurrentTempo(90);

        //Play arpeggio
        player.playHarmony(new Chord("C", "maj"), 0, null, globalFeatures);
    }
}
