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
package music.markov;

import music.elements.Sequence;
import music.elements.Playable;
import music.elements.Chord;
import java.util.*;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class MarkovModel {

    //Final variables not tested
    private final Playable modelType;
    private final int depth;
    private final int length;
    private final int tableHeight;
    private ArrayList<Sequence> inputSequences; // Dispose???
    int[][] markovTable; // oh well
    Queue<Playable> trainStream;
    Queue<Playable> liveStream;

    private int emptyInputSequences;
    private int allPlayablesRecorded;

    /**
     *
     * @param depth
     * @param modelType
     */
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

    /**
     *
     * @param inputSequences
     */
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

    /**
     *
     * @param playable
     */
    public void livePush(Playable playable) {
        recordPlayable(liveStream, playable);
    }

    /**
     *
     */
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

    /**
     *
     * @param queryDepth
     * @return
     */
    public ArrayList<Playable> getSortedProbabilities(int queryDepth) {
        ArrayList<Playable> result = getProbabilities(queryDepth);
        if (result == null) {
            return null;
        }
        Collections.sort(result);
        return result;
    }

    /**
     *
     * @param queryDepth
     * @return
     */
    public ArrayList<Playable> getProbabilities(int queryDepth) {
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
        return result;
    }

    /**
     *
     * @param queryDepth
     * @return
     */
    public String printSortedPlayables(int queryDepth) {
        String result = ("Sorted probabilities for live sequence with depth " + queryDepth + "\n");
        Collection<Playable> markovOutput = getSortedProbabilities(queryDepth);
        for (Playable playable : markovOutput) {
            result += (((Chord) playable).getNameAndProbability() + " (" + playable.getCount() + " out of " + playable.getTotal() + ")\n");
        }
        return result + "\n";
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

    /**
     *
     */
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

    /**
     *
     * @return
     */
    public ArrayList<Playable> getAllInputSequences() {
        ArrayList<Playable> result = new ArrayList<>();
        for (Sequence sequence : inputSequences) {
            result.addAll(sequence.getSequence());
        }
        return result;
    }

    /**
     *
     * @return
     */
    public int getEmptyInputSequenceCount() {
        return emptyInputSequences;
    }

    /**
     *
     * @return
     */
    public int getTableSize() {
        return markovTable.length;
    }

    /**
     *
     * @return
     */
    public int getAllPlayablesRecorded() {
        return allPlayablesRecorded;
    }

    /**
     *
     */
    public void testMethod() {
        int testSum = 0;
        for (int i = 1; i <= 5000; i++) {
            testSum += markovTable[markovTable.length - i][length];
        }
        //System.out.println("Last 5000 elements total:" + testSum);
    }

    //Added in April

    /**
     *
     * @param queryDepth
     * @return
     */
        public ArrayList<Playable> getProcessedProbabilities(int queryDepth) {
        ArrayList<Playable> result = getProbabilities(queryDepth);

        for (int i = 0; i < length; i++) {
            Playable element = result.get(i);
            for (Playable livePlayable : liveStream) {
                if (element.toString().equals(livePlayable.toString())) {
                    //Process logic - set probability to 0
                    element.setProbability(0);
                    break;
                }
            }
        }
        return result;
    }

    /**
     *
     * @return
     */
    public ArrayList<Playable> getCondensedProcessedProbabilities() {

        ArrayList<Playable> utilities = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Playable newElement = modelType.getNewPlayableFromMarkovNumeric(i);
            if (liveStream.isEmpty()) {
                newElement.setProbability(markovTable[0][i]);
            } else {
                newElement.setProbability(0);
            }
            utilities.add(newElement);
        }

        for (int i = 1; i <= depth; i++) {
            if (i > liveStream.size()) {
                break;
            }
            ArrayList<Playable> probabilities = getProcessedProbabilities(i);
            for (int j = 0; j < length; j++) {
                utilities.get(j).increaseProbability(probabilities.get(j).getProbability() * i);
            }

        }
        return utilities;
    }

    /**
     *
     * @return
     */
    public ArrayList<Playable> getCondensedProcessedSortedProbabilities() {
        ArrayList<Playable> result = getCondensedProcessedProbabilities();
        Collections.sort(result);
        return result;
    }

    /**
     *
     * @return
     */
    public String getCondensedProcessedSortedProbabilityString() {
        String result = "\n\n--Initial playable probabilities:\n" + printSortedPlayables(0) + "\n";
        result += "Next playable condensed probabilities:\n";
        ArrayList<Playable> probabilities = getCondensedProcessedSortedProbabilities();
        for (int i = 0; i < probabilities.size(); i++) {
            Playable playable = probabilities.get(i);
            result += "\t" + playable.toString() + " - " + playable.getRoundedProbability() + "\n";
        }
        return result;
    }

    /**
     *
     * @return
     */
    public Playable getTopCondensedProcessedPlayable() {
        return getCondensedProcessedSortedProbabilities().get(0);
    }

    /**
     *
     * @return
     */
    public Playable getNextPlayable() {
        Playable nextPlayable = getTopCondensedProcessedPlayable();
        return nextPlayable;
    }

}
