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
package music.extractors.instrument;

import java.io.File;
import java.util.Arrays;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * The custom developed instrument extractor
 * @author Martin Minovski <martin at minovski.net>
 */
public class InstrumentExtractor {


    /**
     * Returns the first instrument in the event stream
     * @param file A single midi file to extract from
     * @return The MIDI value of the chosen instrument
     */
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

    /**
     * A static method which returns the most frequently used instrument
     * @param files MIDI files to extract from
     * @return
     */
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

    /**
     * A static method which returns the most frequently used instruments
     * @param files MIDI files
     * @return
     */
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
