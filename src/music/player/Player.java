/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Martin
 */
public class Player {

    Random rand;
    final Synthesizer synth;
    final MidiChannel[] channels;

    int[] instrumentToChannelMap;
    int channelCounter;


    public static void main(String[] args) {
        Player player = new Player();
        player.playArpeggio(new Chord("C", "maj"), 0, 1600, 4, 70);
    }

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
