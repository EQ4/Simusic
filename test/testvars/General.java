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
package testvars;

import extractors.ContentExtractTest;
import java.io.File;
import music.elements.Chord;
import java.util.*;
import music.markov.MarkovModel;
import music.extractors.content.ContentExtractor;
import music.elements.Playable;
import music.elements.Sequence;
import music.extractors.feature.FeatureExtractor;
import java.io.FileInputStream;
import java.io.InputStream;
import music.player.Player;
import run.Main;
import static run.Main.player;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * @author Martin
 */
public class General {

    //public static String midiPath = "runtime/test_files/midi/imagine.mid";
    public static String midiPath = "runtime/grouped_agents/Group3/Agent5";
    public static String featurePath = "runtime/grouped_agents/Group3/Agent5/simusic/features";
    //public static String featurePath = "runtime/test_files/features";

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        // Nothing - this class is used to hold static test variables. 
        // To test, run files in other test packages.
    }

    public static File[] getFileArrayFromPathString(String path) {
        File inputPath = new File(path);
        File[] fileArray;
        if (inputPath.isDirectory()) {
            fileArray = inputPath.listFiles();
        } else {
            fileArray = new File[]{inputPath};
        }
        return fileArray;
    }

}
