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

import extractors.ChordExtractorTest;
import static extractors.ChordExtractorTest.extractChords;
import java.util.ArrayList;
import music.elements.Chord;
import music.elements.Playable;
import music.elements.Sequence;
import music.markov.MarkovModel;
import run.AllTests;
import static run.AllTests.featurePath;
import static run.AllTests.midiPath;
import run.Main;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class MarkovTest {

    static MarkovModel trainMarkov(ArrayList<Sequence> chords) {
        //Train new markov model
        MarkovModel markovModel = new MarkovModel(3, new Chord());
        markovModel.trainModel(chords);
        System.out.println(markovModel.toString());
        return markovModel;
    }

    public static void main(String[] args) {
        //Extract chords first
        ArrayList<Sequence> chords = extractChords(AllTests.midiPath);

        //Train Markov model
        MarkovModel markovModel = trainMarkov(chords);

        //Add chords to live sequence
        while (true) {
            Playable nextChord = markovModel.getTopCondensedProcessedPlayable();
            markovModel.livePush(nextChord);
            System.out.println(nextChord.toString());
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
