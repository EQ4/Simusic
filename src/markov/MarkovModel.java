/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package markov;

import elements.*;
import java.util.*;

/**
 *
 * @author Martin
 */
public class MarkovModel {

    private int markovSize;
    private int depth;
    private ArrayList<Sequence> inputSequences;
    private Playable.Type playableType;
    Instance rootInstance;
    Queue<Playable> liveStream;

    public MarkovModel(int depth, Playable.Type playableType) {
        this.depth = depth;
        this.playableType = playableType;
        switch (playableType) {
            case CHORD:
                markovSize = Chord.maxMarkovInteger;
                break;
            case NOTE:
                markovSize = Note.maxMarkovInteger;
                break;
            default:
                break;
        }
        this.rootInstance = new Instance(depth, markovSize);
        this.liveStream = new LinkedList<>();
    }

    public void trainModel(ArrayList<Sequence> inputSequences) {
        this.inputSequences = inputSequences;
        for (Sequence sequence : inputSequences) {
            if (!sequence.isEmpty()) {
                int[] markovSequence = sequence.getMarkovIntegerArray();
                //Update tree at each new playable
                for (int i = 0; i < sequence.getSize() - depth; i++) {
                    rootInstance.updateTree(markovSequence, i);
                }
            }
        }
    }
    
    public void liveRecord(Playable playable) {
        if (liveStream.size() == depth-1) {
            liveStream.remove();
        }
        liveStream.add(playable);
    }
    
    //TO FIX!!!
    public Collection<Playable> getSortedProbabilitiesForLiveStream(int depth) {
        if (depth > this.depth-1) {
            //Error.
            return null;
        }
        Object[] streamArray = liveStream.toArray();
        int[] streamIntArray = new int[streamArray.length];
        for (int i = 0; i < streamArray.length; i++) {
            streamIntArray[i] = ((Playable)streamArray[i]).getMarkovInteger();
        }
        double[] probabilities = rootInstance.getProbabilities(streamIntArray, depth);
        Collection<Playable> = new Collection<>();
        
    }

    public Sequence getAllInputSequences() {
        Sequence outputPlayables = new Sequence(playableType);
        for (Sequence sequence : inputSequences) {
            ArrayList<Playable> subList = sequence.getSequence();
            for (Playable playable : subList) {
                outputPlayables.addPlayable(playable);
            }
        }
        return outputPlayables;
    }

    public Sequence getInputSequence(int number) {
        return inputSequences.get(number);
    }

    public String printAllInputSequeces() {
        String result = "Input sequences:\n";
        for (int i = 0; i < inputSequences.size(); i++) {
            ArrayList<Playable> subList = inputSequences.get(i).getSequence();
            result += i + ": ";
            for (Playable playable : subList) {
                result += playable.toString() + " ";
            }
            result += "<End>\n";
        }
        return result;
    }

    public Sequence getTestSequence() {
        // TODO!

        return getAllInputSequences();
    }
}
