/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package markov;

import elements.Playable;
import elements.Sequence;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Martin
 */
public class Instance {

    int total; // Only on branch nodes!!!
    int value;
    int currentLevel;
    Instance[] subInstances;

    public Instance(int level, int numberOfChildren) {
        this.total = 0; // Branch only
        this.value = 0; // Leaf + Branch
        this.currentLevel = level - 1;
        this.subInstances = new Instance[numberOfChildren];
        if (currentLevel > 0) {
            for (int i = 0; i < numberOfChildren; i++) {
                subInstances[i] = new Instance(currentLevel, numberOfChildren);
            }
        }
    }

    public void updateTree(int[] sequenceMarkovIntegerArray, int startingPoint) {
        value++;
        if ((currentLevel > 0) && (startingPoint < sequenceMarkovIntegerArray.length)) {
            subInstances[sequenceMarkovIntegerArray[startingPoint]].updateTree(sequenceMarkovIntegerArray, startingPoint++);
            total++;
        }
    }

    public double[] getProbabilities(int[] liveSequence, int pointer) {
        if (pointer < liveSequence.length - 1) {
            return subInstances[liveSequence[pointer]].getProbabilities(liveSequence, pointer++);
        }
        else { // if (pointer == liveSequence.length - 2) <OLD>
            double[] result = new double[subInstances.length];
            for (int i = 0; i < subInstances.length; i++) {
                result[i] = subInstances[i].getValue() / (double)total;
            }
            return result;
        }
    }
    
    public double getValue(){
        return (double)value;
    }
}
