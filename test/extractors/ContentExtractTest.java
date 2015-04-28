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
import music.extractors.content.ContentExtractor;
import testvars.General;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class ContentExtractTest {

    public static void main(String[] args) {

        File[] midiFileArray = General.getFileArrayFromPathString(General.midiPath);
        ContentExtractor cextract = new ContentExtractor(midiFileArray, null);

        ArrayList<Sequence> harmonySequences = cextract.getHarmonySequences();
        ArrayList<Sequence> melodySequences = cextract.getMelodySequences();

        //Print chord sequences
        System.out.println("\nPrinting all extracted HARMONY sequences...\n");
        for (Sequence sequence : harmonySequences) {
            System.out.println("\tSource: " + sequence.getMIDISource() + ", winning harmony channel " + sequence.getMIDIChannel() + ", song key: " + sequence.getSongKey().toString() + "\n"
                    + "\tChord Sequence: " + sequence.toString() + "\n");
        }
        System.out.println("\nPrinting all extracted MELODY sequences...\n");
        for (Sequence sequence : melodySequences) {
            System.out.println("\tSource: " + sequence.getMIDISource() + ", winning melody channel " + sequence.getMIDIChannel() + ", song key: " + sequence.getSongKey().toString() + "\n"
                    + "\tNote Sequence: " + sequence.toString() + "\n");
        }
    }
}
