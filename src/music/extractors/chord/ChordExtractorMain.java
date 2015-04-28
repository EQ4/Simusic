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

import music.elements.Chord;
import music.elements.Playable;
import music.elements.Sequence;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import rmi.agents.Agent;
import sun.security.krb5.internal.SeqNumber;

/**
 * Main Chord Extractor class
 * Created and used by agents
 * @author Martin Minovski <martin at minovski.net>
 */
public class ChordExtractorMain {

    /**
     * Extracts chord sequences from MIDI files
     * Lists the sequence of every file as a new Sequence object
     * @param files The array of MIDI file paths to extract from
     * @param callingAgent Callback agent object - used for logging
     * @return An ArrayList of Chord Sequences
     */
    public static ArrayList<Sequence> extractChordsFromMidiFiles(File[] files, Agent callingAgent) {
        boolean agentIsLogging = (callingAgent != null);

        ArrayList<Sequence> fullSequence = new ArrayList<>();

        if (agentIsLogging) {
            callingAgent.log("Extracting chords from " + files.length + " files...", false);
        }

        for (int i = 0; i < files.length; i++) {

            if (agentIsLogging) {
                callingAgent.log("Extracting chords - file " + (i + 1) + "/" + files.length, false);
            }

            Sequence normalizedSequence = new Sequence();
            try {
                if (files[i].isFile()) {
                    String file = files[i].getPath();

                    ChordScanner scanner = new ChordScanner(file);

                    ArrayList<Chord> sequence = scanner.getSequence();
                    if (sequence.isEmpty()) {
                        //Commented out - the Markov model handles and records empty sequences
                        //Commented in  - exception occurs
                        continue;
                    }
                    Chord songKey = getMainKey(sequence);

                    int songKeyInt = songKey.getBaseNumeric();
                    int songOffset = 24 - songKeyInt;

                    if (!songKey.isMajor()) {
                        songOffset = songOffset - 3;
                    }

                    for (int j = 0; j < sequence.size(); j++) {
                        Chord chord = sequence.get(j);
                        normalizedSequence.addPlayable(chord.getTransposedTwinChord(songOffset));
                    }
                    
                    //Set winning channel (+ 1 because MIDI channels start from 1 outside programming)
                    normalizedSequence.setHarmonyChannel(scanner.getWinningChannel() + 1);
                    
                    //Set song key
                    normalizedSequence.setSongKey(songKey);

                }
            } catch (Exception e) {
                if (agentIsLogging) {
                    callingAgent.log("Chord Extractor exception.", false);
                }
                e.printStackTrace();
            }
            normalizedSequence.setMIDISource(files[i].getName());
            fullSequence.add(normalizedSequence);
        }

        if (agentIsLogging) {
            callingAgent.log("Chords have been extracted!", false);
        }

        return fullSequence;

    }

    private static Chord getMainKey(ArrayList<Chord> sequence) {
        Map<String, Integer> chordHash = new HashMap<>();
        for (int j = 0; j < sequence.size(); j++) {
            String chordName = sequence.get(j).toString();
            if (chordHash.containsKey(chordName)) {
                chordHash.put(chordName, (Integer) chordHash.get(chordName) + 1);
            } else {
                chordHash.put(chordName, 1);
            }
        }

        Map.Entry<String, Integer> maxEntry = null;

        for (Map.Entry<String, Integer> entry : chordHash.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        String[] result = maxEntry.getKey().split("-");;
        return new Chord(result[0], result[1]);
    }
}
