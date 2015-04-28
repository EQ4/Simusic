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
package extractors;

import java.io.File;
import java.util.ArrayList;
import music.elements.Sequence;
import music.extractors.chord.ChordExtractorMain;
import run.AllTests;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class ChordExtractorTest {

    public static void main(String[] args) {
        ArrayList<Sequence> sequences = extractChords(AllTests.midiPath);
        //Print chord sequences
        System.out.println("Printing all extracted chord sequences...");
        for (Sequence sequence : sequences) {
            System.out.println("Source: " + sequence.getMIDISource() + ", harmony channel " + sequence.getHarmonyChannel() + ", song key: " + sequence.getSongKey().toString() + "\n"
                    + "Sequence: " + sequence.toString() + "\n");
        }
    }

    public static ArrayList<Sequence> extractChords(String fileOrFolderPath) {
        File[] midiFileArray = AllTests.getFileArrayFromPathString(fileOrFolderPath);
        return ChordExtractorMain.extractChordsFromMidiFiles(midiFileArray, null);
    }
}
