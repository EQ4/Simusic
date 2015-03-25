/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chordextract;

import elements.Chord;
import elements.Playable;
import elements.Sequence;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Martin
 */
public class ChordExtractor {
    
    public static ArrayList<Sequence> extractChordsFromMidiFiles(String path) {

        File folder = new File(path);
        File[] files = folder.listFiles();

        ArrayList<Sequence> fullSequence = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            Sequence normalizedSequence = new Sequence(Playable.Type.CHORD);
            try {
                if (files[i].isFile()) {
                    String file = files[i].getPath();
                    
                    ChordScanner ce = new ChordScanner(file);

                    ArrayList<Chord> sequence = ce.getSequence();
                    if (sequence.isEmpty()) {
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
                //Don't care.
            }
            fullSequence.add(normalizedSequence);
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
