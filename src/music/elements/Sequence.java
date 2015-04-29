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
package music.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A sequence of playables
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class Sequence {

    private ArrayList<Playable> sequence;
    private String MIDIFileSource;
    private Integer MIDIChannel;
    private Chord songKey;

    /**
     * Default constructor
     */
    public Sequence() {
        this.sequence = new ArrayList<>();
        
        //Initially null; set later
        this.MIDIFileSource = null;
        this.MIDIChannel = null;
        this.songKey = null;
    }

    /**
     * Create sequence from ArrayList of Playables
     * @param sequence ArrayList to read from
     */
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
    
    public void setMIDISource(String source) {
        MIDIFileSource = source;
    }
    
    public String getMIDISource() {
        return MIDIFileSource;
    }
    
    public void setMIDIChannel(int channelNumber) {
        MIDIChannel = channelNumber;
    }
    
    public Integer getMIDIChannel() {
        return MIDIChannel;
    }
    
    public void setSongKey(Chord chord) {
        songKey = chord;
    }
    
    public Chord getSongKey() {
        return songKey;
    }

    /**
     * Returns the sequence array list
     * @return Sequence array list
     */
    public ArrayList<Playable> getSequence() {
        return sequence;
    }

    /**
     * Returns an array of Markov integers for each Playable in the sequence
     * @return An int array
     */
    public int[] getMarkovIntegerArray() {
        int[] result = new int[sequence.size()];
        int counter = 0;
        for (Playable playable : sequence) {
            result[counter++] = playable.getMarkovInteger();
        }
        return result;
    }

    /**
     * Returns the number of Playables in the Sequence
     * @return
     */
    public int getSize() {
        return sequence.size();
    }

    /**
     * Adds a new playable to the sequence. Used mostly by extractors
     * @param playable The new playable to add
     */
    public void appendPlayable(Playable playable) {
        sequence.add(playable);
    }

    /**
     * Checks if the sequence is empty
     * @return True if empty sequence
     */
    public boolean isEmpty() {
        return sequence.isEmpty();
    }

    @Override
    public String toString() {
        String result = "";
        for (Playable playable : sequence) {
            result += playable.toString() + " ";
        }
        return result;
    }
}
