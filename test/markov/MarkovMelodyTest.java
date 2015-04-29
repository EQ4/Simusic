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
package markov;

import java.io.File;
import java.util.ArrayList;
import music.elements.Note;
import music.elements.Playable;
import music.elements.Sequence;
import music.extractors.content.ContentExtractor;
import music.markov.MarkovModel;
import testvars.General;
import run.Main;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class MarkovMelodyTest {

    static MarkovModel trainMarkov(ArrayList<Sequence> notes) {
        //Train new markov model
        MarkovModel markovModel = new MarkovModel(3, new Note());
        markovModel.trainModel(notes);
        System.out.println(markovModel.toString());
        return markovModel;
    }

    public static void main(String[] args) {
        //Extract chords first
        File[] midiFileList = General.getFileArrayFromPathString(General.midiPath);
        ContentExtractor cextract = new ContentExtractor(midiFileList, null);
        ArrayList<Sequence> melodySequences = cextract.getMelodySequences();

        //Train Markov model
        MarkovModel markovModel = trainMarkov(melodySequences);

        //Add chords to live sequence
        while (true) {
            Playable nextNote = markovModel.getTopCondensedProcessedPlayable();
            markovModel.livePush(nextNote);
            System.out.println(nextNote.toString());
            Main.wait(400);
        }

        /*
         markovModel.livePush(new Chord("G", "maj"));
         markovModel.livePush(new Chord("F", "maj"));
         markovModel.livePush(new Chord("C", "maj"));
         markovModel.livePush(new Chord("A", "min"));
         markovModel.livePush(new Chord("E", "maj"));
         markovModel.livePush(new Chord("F", "maj"));
         markovModel.livePush(new Chord("G", "maj"));

         //Print condensed probabilities
         System.out.println(markovModel.getCondensedSortedProbabilityString());
                
         */
    }
}
