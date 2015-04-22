/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package music.player;

import java.util.Random;

/**
 *
 * @author Martin
 */
public final class TonePicker {

    public static void main(String[] args) {
        TonePicker tp = new TonePicker(false, new Random());
        for (int i = 0; i < 100; i++) {
            System.out.println(tp.getNextTone());
        }
    }

    Random rand;
    final int[] majorTones = new int[]{0, 4, 7};
    final int[] minorTones = new int[]{0, 3, 7};

    int counter;
    int[] tones;
    boolean isMajor;

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
