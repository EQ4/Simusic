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
import music.extractors.instrument.InstrumentExtractor;
import static music.extractors.instrument.InstrumentExtractor.getMostFrequentInstrument;
import testvars.General;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class InstrumentExtractorTest {

    public static void main(String[] args) {
        File letItBe = new File(General.midiPath + "/let_it_be.mid");
        File imagine = new File(General.midiPath + "/imagine.mid");
        File[] file = new File[] {letItBe, imagine};
        System.out.println(getMostFrequentInstrument(file));
    }
}
