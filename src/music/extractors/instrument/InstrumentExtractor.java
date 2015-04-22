/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package music.extractors.instrument;

import java.io.File;
import java.util.Arrays;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 *
 * @author Martin
 */
public class InstrumentExtractor {

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        String path = "E:\\Documents\\NetBeansProjects\\Simusic\\runtime\\sample_agents\\Group1\\Doug";
        System.out.println(Arrays.toString(getMostFrequentInstruments(new File(path).listFiles())));
        System.out.println(getMostFrequentInstrument(new File(path).listFiles()));
    }

    public static int getFirstInstrument(File file) {
        try {
            Sequence sequence = MidiSystem.getSequence(file);
            for (Track track : sequence.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    try {
                        ShortMessage message = (ShortMessage) track.get(i).getMessage();
                        int status = message.getStatus();
                        int data = message.getData1();
                        if ((status >> 4) == 12) {
                            return data;
                        }
                    } catch (java.lang.ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getMostFrequentInstrument(File[] files) {
        int[] instruments = getMostFrequentInstruments(files);

        int instrument = 0;
        int max = 0;

        for (int i = 0; i < 128; i++) {
            if (instruments[i] > max) {
                max = instruments[i];
                instrument = i;
            }
        }

        return instrument;
    }

    public static int[] getMostFrequentInstruments(File[] files) {
        int[] instruments = new int[128];
        for (int i = 0; i < 128; i++) {
            instruments[i] = 0;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            try {
                Sequence sequence = MidiSystem.getSequence(file);
                for (Track track : sequence.getTracks()) {
                    for (int i = 0; i < track.size(); i++) {
                        try {
                            ShortMessage message = (ShortMessage) track.get(i).getMessage();
                            int status = message.getStatus();
                            int data = message.getData1();
                            if ((data > 127) || (data == 0)) {
                                continue;
                            }
                            if ((status >> 4) == 12) {
                                instruments[data]++;
                            }
                        } catch (java.lang.ClassCastException e) {
                            //e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instruments;
    }
}
