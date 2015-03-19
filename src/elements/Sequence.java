/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elements;

import java.util.ArrayList;

/**
 *
 * @author Martin
 */
public class Sequence {

    private ArrayList<Playable> sequence;
    private Playable.Type type;

    public Sequence(Playable.Type type) {
        this.sequence = new ArrayList<>();
        this.type = type;
    }
    
    public Sequence(ArrayList<Object> sequence, Playable.Type type) {
        this.sequence = new ArrayList<>();
        this.type = type;
        for (Object playable : sequence) {
            try {
                this.sequence.add((Playable)playable);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
    }
    
    public Playable.Type getType() {
        return type;
    }
    
    public ArrayList<Playable> getSequence() {
        return sequence;
    }
    
    public void addPlayable(Playable playable) {
        sequence.add(playable);
    }
}
