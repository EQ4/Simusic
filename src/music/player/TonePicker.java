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
package music.player;

import java.util.Random;

/**
 * This class selects tones that have not been played the last 4 beats
 * @author Martin Minovski <martin at minovski.net>
 */
public final class TonePicker {

    Random rand;
    final int[] majorTones = new int[]{0, 4, 7};
    final int[] minorTones = new int[]{0, 3, 7};

    int counter;
    int[] tones;
    boolean isMajor;

    /**
     * Default constructor
     * @param isMajor
     * @param rand
     */
    public TonePicker(boolean isMajor, Random rand) {
        this.isMajor = isMajor;
        this.rand = rand;
        resetPicker();
    }

    void resetPicker() {
        if (isMajor) {
            tones = copyArray(majorTones);
        } else {
            tones = copyArray(minorTones);
        }
        counter = tones.length;
    }

    int[] copyArray(int[] original) {
        int[] copy = new int[original.length];
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }

    /**
     * Selects the next tone
     * that has not been played the last 4 beats
     * @return The MIDI pitch value
     */
    public int getNextTone() {
        while (counter > 0) {
            int random = rand.nextInt(counter--);
            int tone = tones[random];
            tones[random] = tones[counter];
            return tone;
        }
        resetPicker();
        return getNextTone();
    }

}
