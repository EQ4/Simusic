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

import java.io.EOFException;
import music.elements.Chord;
import music.elements.Sequence;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.sound.midi.InvalidMidiDataException;
import music.elements.Note;
import rmi.agents.Agent;

/**
 * Main Chord Extractor class Created and used by agents
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class ContentExtractor {

    File[] files;
    Agent callingAgent;
    ArrayList<Sequence> allChordSequences;
    ArrayList<Sequence> allNoteSequences;

    public ContentExtractor(File[] files, Agent callingAgent) {
        this.files = files;
        this.callingAgent = callingAgent;
        allChordSequences = new ArrayList<>();
        allNoteSequences = new ArrayList<>();

        extractContent();
    }

    private boolean isAgentLogging() {
        return !(callingAgent == null);
    }

    public final void extractContent() {

        if (isAgentLogging()) {
            callingAgent.log("Extracting chords from " + files.length + " files...", false);
        }

        for (int i = 0; i < files.length; i++) {

            if (isAgentLogging()) {
                callingAgent.log("Extracting chords - file " + (i + 1) + "/" + files.length, false);
            }

            Sequence currentHarmonySequence = new Sequence();
            Sequence currentMelodySequence = new Sequence();
            try {
                if (files[i].isFile()) {
                    String file = files[i].getPath();

                    SequentialScanner scanner;
                    try {
                        scanner = new SequentialScanner(file);
                    } //If file cannot be loaded, continue to next file
                    catch (InvalidMidiDataException | EOFException e) {
                        if (isAgentLogging()) {
                            callingAgent.log("Error while extracting from file " + (i + 1) + ": " + e.getLocalizedMessage(), false);
                        } else {
                            System.out.println("Error while extracting from file " + (i + 1) + ": " + e.getLocalizedMessage());
                        }
                        continue;
                    }

                    ArrayList<Chord> harmonySequence = scanner.getHarmonySequence();
                    if (harmonySequence.isEmpty()) {
                        //If harmony sequence is empty, continue to next file
                        //Song key cannot be determined, so melody extraction is pointless
                        continue;
                    }

                    //Normalize sequence to C Major:
                    Chord songKey = getMainKey(harmonySequence);

                    int songKeyInt = songKey.getBaseNumeric();
                    int songOffset = 24 - songKeyInt;

                    // IMPORTANT:
                    // If the key is minor, consider it the parallel minor in order to normalize to only major keys
                    if (!songKey.isMajor()) {
                        songOffset -= 3;
                    }
                    
                    //Purify offset
                    songOffset %= 12;

                    for (int j = 0; j < harmonySequence.size(); j++) {
                        Chord chord = harmonySequence.get(j);
                        currentHarmonySequence.addPlayable(chord.getTransposedTwinChord(songOffset));
                    }

                    //Set winning channel (+ 1 because MIDI channels start from 1 outside programming)
                    currentHarmonySequence.setMIDIChannel(scanner.getWinningHarmonyChannel() + 1);
                    //Set song key
                    currentHarmonySequence.setSongKey(songKey);

                    //Now extract melody
                    ArrayList<Note> melodySequence = scanner.getMelodySequence();
                    if (harmonySequence.isEmpty()) {
                        continue;
                    }

                    for (Note note : melodySequence) {
                        int noteInt = (note.getMarkovInteger() + songOffset) % 12;
                        currentMelodySequence.addPlayable(Note.integerToNewNote(noteInt));
                    }

                    //Set winning channel (+ 1 because MIDI channels start from 1 outside programming)
                    currentMelodySequence.setMIDIChannel(scanner.getWinningMelodyChannel() + 1);
                    //Set song key
                    currentMelodySequence.setSongKey(songKey);

                }
            } catch (Exception e) {
                if (isAgentLogging()) {
                    callingAgent.log("Chord Extraction exception.", false);
                }
                e.printStackTrace();
            }
            currentHarmonySequence.setMIDISource(files[i].getName());
            allChordSequences.add(currentHarmonySequence);

            currentMelodySequence.setMIDISource(files[i].getName());
            allNoteSequences.add(currentMelodySequence);
        }

        if (isAgentLogging()) {
            callingAgent.log("Chords have been extracted!", false);
        }
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

    public ArrayList<Sequence> getHarmonySequences() {
        return allChordSequences;
    }

    public ArrayList<Sequence> getMelodySequences() {
        return allNoteSequences;
    }

}
