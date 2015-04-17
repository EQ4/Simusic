/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package music.elements;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class Sequence {

    private ArrayList<Playable> sequence;

    public Sequence() {
        this.sequence = new ArrayList<>();
    }

    public Sequence(ArrayList<Object> sequence) {
        this.sequence = new ArrayList<>();
        for (Object playable : sequence) {
            try {
                this.sequence.add((Playable) playable);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
    }


    public ArrayList<Playable> getSequence() {
        return sequence;
    }
    
    public int[] getMarkovIntegerArray() {
        int[] result = new int[sequence.size()];
        int counter = 0;
        for (Playable playable : sequence) {
            result[counter++] = playable.getMarkovInteger();
        }
        return result;
    }

    public int getSize() {
        return sequence.size();
    }

    public void addPlayable(Playable playable) {
        sequence.add(playable);
    }
    
    public boolean isEmpty() {
        return sequence.isEmpty();
    }
}
