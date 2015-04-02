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

    private Playable modelType;
    private int depth;
    private int length;
    private int tableHeight;
    private ArrayList<Sequence> inputSequences; // Dispose???
    int[][] markovTable; // oh well
    Queue<Playable> trainStream;
    Queue<Playable> liveStream;
    
    private int emptyInputSequences;
    private int allPlayablesRecorded;

    public MarkovModel(int depth, Playable modelType) {
        this.modelType = modelType;
        this.depth = depth;
        this.length = modelType.getMaximumMarkovInteger();

        this.trainStream = new LinkedList<>();
        this.liveStream = new LinkedList<>();

        this.tableHeight = calculateTableHeight(length, depth);
        initializeTable();

        emptyInputSequences = 0;
        allPlayablesRecorded = 0;
    }

    private void initializeTable() {
        markovTable = new int[tableHeight + 1][length + 1];

        for (int i = 0; i <= tableHeight; i++) {
            for (int j = 0; j <= length; j++) {
                markovTable[i][j] = 0;
            }
        }
    }

    private int calculateTableHeight(int length, int depth) {
        if (depth == 1) {
            return length;
        }
        return calculateTableHeight(length, depth - 1) + getPower(length, depth);
    }

    private int getPower(int base, int exponent) {
        if (exponent > 0) {
            return getPower(base, exponent - 1) * base;
        }
        return 1;
    }

    public void trainModel(ArrayList<Sequence> inputSequences) {
        this.inputSequences = inputSequences;
        for (Sequence sequence : inputSequences) {
            if (!sequence.isEmpty()) {
                for (Playable playable : sequence.getSequence()) {
                    // Update table:
                    if (trainStream.isEmpty()) {
                        // Update initial chord
                        markovTable[0][playable.getMarkovInteger()]++;   //Updates count
                        markovTable[0][length]++;   // Updates total
                    } else {
                        // Normal table update
                        for (int i = 1; i <= trainStream.size(); i++) {
                            updateTable(i, playable);
                        }
                    }

                    //Record chord onto stream
                    recordPlayable(trainStream, playable);
                    
                    //Just for the record
                    allPlayablesRecorded++;
                }
                trainStream.clear();
            } else {
                emptyInputSequences++;
            }
        }
    }

    private void recordPlayable(Queue<Playable> stream, Playable playable) {
        if (stream.size() >= depth) {
            stream.remove();
        }
        stream.add(playable);
    }

    public void livePush(Playable playable) {
        recordPlayable(liveStream, playable);
    }

    public void liveFlush() {
        liveStream.clear();
    }

    private void updateTable(int updateDepth, Playable newPlayable) {
        int[] streamIntArray = getPartialStreamIntArray(updateDepth, trainStream);
        int row = getMarkovRowUsingDaFormula(streamIntArray);
        markovTable[row][newPlayable.getMarkovInteger()]++;
        markovTable[row][length]++;
    }

    private int[] getPartialStreamIntArray(int windowSize, Queue<Playable> stream) {
        Object[] streamArray = stream.toArray();
        int[] streamIntArray = new int[windowSize];
        for (int i = 0; i < windowSize; i++) {
            streamIntArray[i] = ((Playable) streamArray[i + (streamArray.length - windowSize)]).getMarkovInteger();
        }
        return streamIntArray;
    }

    private int getMarkovRowUsingDaFormula(int[] inputArray) {
        int row = 0;
        for (int i = 0; i < inputArray.length; i++) {
            // Da Markov formula
            row += (1 + inputArray[i]) * getPower(length, inputArray.length - i - 1);
        }
        return row;
    }

    public ArrayList<Playable> getSortedPlayables(int queryDepth) {
        if (queryDepth > depth) { // -1 ?
            //Error. Query depth cannot be larger than model depth
            return null;
        }

        ArrayList<Playable> result = new ArrayList<>();
        int[] streamIntArray = getPartialStreamIntArray(queryDepth, liveStream);
        int row = getMarkovRowUsingDaFormula(streamIntArray);
        for (int i = 0; i < length; i++) {
            Playable newElement = modelType.getNewPlayableFromMarkovNumeric(i);
            newElement.setProbability((double) markovTable[row][i] / (double) markovTable[row][length]);
            newElement.setCount(markovTable[row][i]);
            newElement.setTotal(markovTable[row][length]);
            result.add(newElement);
        }
        Collections.sort(result);
        return result;

    }

    public void printSortedPlayables(int queryDepth) {
        System.out.println("Sorted probabilities for live sequence with depth " + queryDepth);
        Collection<Playable> markovOutput = getSortedPlayables(queryDepth);
        for (Playable playable : markovOutput) {
            System.out.println(((Chord) playable).getNameAndProbability() + " (" + playable.getCount() + " out of " + playable.getTotal() + ")");
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Markov Model Table:\n\t\t");
        //Headings
        for (int i = 0; i < length; i++) {
            result.append(modelType.getNewPlayableFromMarkovNumeric(i).toString());
            result.append("\t");
        }
        result.append("Total \nInitial\t\t");

        //Table
        for (int i = 0; i < markovTable.length; i++) {
            if (i == 0) {
                for (int j = 0; j <= length; j++) {
                    result.append(markovTable[0][j]);
                    result.append("\t");
                }
                result.append("\n");
                continue;
            }


            int row = i - 1;
            if (row < length) {
                result.append(modelType.getNewPlayableFromMarkovNumeric(row));
            } else {
                result.append(row);
            }


            result.append("\t\t");
            for (int j = 0; j <= length; j++) {
                result.append(markovTable[i][j]);
                result.append("\t");
            }
            result.append("\n");
        }
        return result.toString();
    }

    public void printAllInputSequeces() {
        String result = "Input sequences:\n";
        for (int i = 0; i < inputSequences.size(); i++) {
            ArrayList<Playable> subList = inputSequences.get(i).getSequence();
            result += i + ": ";
            for (Playable playable : subList) {
                result += playable.toString() + " ";
            }
            result += "<End>\n";
        }
        System.out.println(result);
    }
    
    public ArrayList<Playable> getAllInputSequences() {
        ArrayList<Playable> result = new ArrayList<>();
        for (Sequence sequence: inputSequences) {
            result.addAll(sequence.getSequence());
        }
        return result;
    }

    public int getEmptyInputSequenceCount() {
        return emptyInputSequences;
    }

    public int getTableSize() {
        return markovTable.length;
    }
    
    public int getAllPlayablesRecorded() {
        return allPlayablesRecorded;
    }

    public void testMethod() {
        int testSum = 0;
        for (int i = 1; i <= 5000; i++) {
            testSum += markovTable[markovTable.length - i][length];
        }
        //System.out.println("Last 5000 elements total:" + testSum);
    }
}
