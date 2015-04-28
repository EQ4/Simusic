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
import music.extractors.feature.FeatureExtractor;
import testvars.General;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class FeatureExtractorTest {

    public static void main(String[] args) {
        //Extract features
        FeatureExtractor fextract = extractChords(General.midiPath, General.featurePath);
        //Print all average features
        System.out.println(fextract.getAverageFeatures());
    }

    public static FeatureExtractor extractChords(String midiPath, String featureFolderPath) {

        File[] midiFileArray = General.getFileArrayFromPathString(midiPath);
        File featureFolder = new File(featureFolderPath);

        //true stands for overwrite XML files
        //null stands for no logger agent
        FeatureExtractor fextract = new FeatureExtractor(midiFileArray, featureFolder, true, null);
        return fextract;
    }
}
