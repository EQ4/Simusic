/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author Martin
 */
public class ChordExtractor {

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

                    ChordScanner ce = new ChordScanner(file);

                    ArrayList<Chord> sequence = ce.getSequence();
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

                }
            } catch (Exception e) {
                if (agentIsLogging) {
                    callingAgent.log("Chord Extractor exception.", false);
                }
                e.printStackTrace();
            }
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
