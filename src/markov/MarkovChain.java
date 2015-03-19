/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package markov;

import elements.Playable;
import elements.Sequence;
import java.io.*;
import java.util.*;

/**
 *
 * @author Martin
 */
public class MarkovChain {
    
    public int markovSize;
    
    private ArrayList<Sequence> inputSequences;
    private Playable.Type playableType;
    private int[] zeroMarkov;
    private int[][] firstMarkov;
    private int[][][] secondMarkov;
    private int[][][][] thirdMarkov;
    private int zeroMarkovTotal;
    private int[] firstMarkovTotals;
    private int[][] secondMarkovTotals;
    private int[][][] thirdMarkovTotals;

    public MarkovChain(ArrayList<Sequence> inputSequences, Playable.Type playableType) {
        this.inputSequences = inputSequences;
        this.playableType = playableType;
        switch (playableType) {
            case CHORD:
                markovSize = 24;
                break;
            case NOTE:
                markovSize = 12;
                break;
            default:
                break;
        }

        initializeMarkovVars();
        generateMarkov();

    }

    private void initializeMarkovVars() {

        zeroMarkov = new int[markovSize];
        firstMarkov = new int[markovSize][markovSize];
        secondMarkov = new int[markovSize][markovSize][markovSize];
        thirdMarkov = new int[markovSize][markovSize][markovSize][markovSize];

        zeroMarkovTotal = 0;
        firstMarkovTotals = new int[markovSize];
        secondMarkovTotals = new int[markovSize][markovSize];
        thirdMarkovTotals = new int[markovSize][markovSize][markovSize];

        for (int i = 0; i < markovSize; i++) {
            firstMarkov[i] = new int[markovSize];
            firstMarkovTotals[i] = 0;
            for (int j = 0; j < markovSize; j++) {
                firstMarkov[i][j] = 0;
                secondMarkov[i][j] = new int[markovSize];
                secondMarkovTotals[i][j] = 0;
                for (int k = 0; k < markovSize; k++) {
                    secondMarkov[i][j][k] = 0;
                    thirdMarkov[i][j][k] = new int[markovSize];
                    thirdMarkovTotals[i][j][k] = 0;
                    for (int l = 0; l < markovSize; l++) {
                        thirdMarkov[i][j][k][l] = 0;
                    }
                }
            }
        }
    }

    private void generateMarkov() {
        for (int i = 0; i < inputSequences.size(); i++) {
            ArrayList<Playable> subList = inputSequences.get(i).getSequence();
            for (int j = 0; j < subList.size(); j++) {
                Playable playable = subList.get(j);
                if (j == 0) {
                    zeroMarkov[playable.getNumericRepresentation()]++;
                    zeroMarkovTotal++;
                }
                if (j > 0) {
                    Playable previousPlayable = subList.get(j - 1);
                    firstMarkov[previousPlayable.getNumericRepresentation()][playable.getNumericRepresentation()]++;
                    firstMarkovTotals[previousPlayable.getNumericRepresentation()]++;
                    if (j > 1) {
                        Playable prePreviousPlayable = subList.get(j - 2);
                        secondMarkov[prePreviousPlayable.getNumericRepresentation()][previousPlayable.getNumericRepresentation()][playable.getNumericRepresentation()]++;
                        secondMarkovTotals[prePreviousPlayable.getNumericRepresentation()][previousPlayable.getNumericRepresentation()]++;
                        if (j > 2) {
                            Playable prePrePreviousPlayable = subList.get(j - 3);
                            thirdMarkov[prePrePreviousPlayable.getNumericRepresentation()][prePreviousPlayable.getNumericRepresentation()][previousPlayable.getNumericRepresentation()][playable.getNumericRepresentation()]++;
                            thirdMarkovTotals[prePrePreviousPlayable.getNumericRepresentation()][prePreviousPlayable.getNumericRepresentation()][previousPlayable.getNumericRepresentation()]++;
                        }
                    }
                }
            }
        }
    }

    public String printFirstMarkovTable() {
        return "First Markov table:\n" + printMarkovTable(firstMarkov, firstMarkovTotals);
    }

    public String getProbabilityTableAfterPlayable(Playable playable) {
        int playableNum = playable.getNumericRepresentation();
        return "Markov table after playable '" + playable.toString() + "':\n" + printMarkovTable(secondMarkov[playableNum], secondMarkovTotals[playableNum]);
    }

    public String getProbabilityTableAfterPlayables(Playable playable1, Playable playable2) {
        int playable1Num = playable1.getNumericRepresentation();
        int playable2Num = playable2.getNumericRepresentation();
        return "Markov table after sequence '" + playable1.toString() + ", " + playable2.toString() + "':\n" + printMarkovTable(thirdMarkov[playable1Num][playable2Num], thirdMarkovTotals[playable1Num][playable2Num]);
    }

    public String printMarkovTable(int[][] table, int[] totals) {
        String result = "Last playable goes on the X-axis.\n***\t<Total>\t";

        for (int i = 0; i < markovSize; i++) {
            result += Playable.getPlayableFromMarkovNumeric(i, playableType).toString() + "\t";
        }
        result += "\n";
        for (int i = 0; i < markovSize; i++) {
            result += Playable.getPlayableFromMarkovNumeric(i, playableType).toString() + "\t";
            result += totals[i] + "\t";
            for (int j = 0; j < markovSize; j++) {
                result += table[i][j] + "\t";
            }
            result += "\n";
        }
        return result;
    }

    public double getProbability(Playable playable) {
        int playableNum = playable.getNumericRepresentation();
        if (zeroMarkovTotal == 0) {
            return 0;
        }
        return (double) zeroMarkov[playableNum] / (double) zeroMarkovTotal;
    }

    public double getProbability(Playable playable1, Playable playable2) {
        int playable1Num = playable1.getNumericRepresentation();
        int playable2Num = playable2.getNumericRepresentation();
        if (firstMarkovTotals[playable1Num] == 0) {
            return 0;
        }
        return (double) firstMarkov[playable1Num][playable2Num] / (double) firstMarkovTotals[playable1Num];
    }

    public double getProbability(Playable playable1, Playable playable2, Playable playable3) {
        int playable1Num = playable1.getNumericRepresentation();
        int playable2Num = playable2.getNumericRepresentation();
        int playable3Num = playable3.getNumericRepresentation();
        if (secondMarkovTotals[playable1Num][playable2Num] == 0) {
            return 0;
        }
        return (double) secondMarkov[playable1Num][playable2Num][playable3Num] / (double) secondMarkovTotals[playable1Num][playable2Num];
    }

    public double getProbability(Playable playable1, Playable playable2, Playable playable3, Playable playable4) {
        int playable1Num = playable1.getNumericRepresentation();
        int playable2Num = playable2.getNumericRepresentation();
        int playable3Num = playable3.getNumericRepresentation();
        int playable4Num = playable4.getNumericRepresentation();
        if (thirdMarkovTotals[playable1Num][playable2Num][playable3Num] == 0) {
            return 0;
        }
        return (double) thirdMarkov[playable1Num][playable2Num][playable3Num][playable4Num] / (double) thirdMarkovTotals[playable1Num][playable2Num][playable3Num];
    }

    public ArrayList<Playable> getSortedProbabilities() {
        return getSortedProbabilities(zeroMarkov, zeroMarkovTotal);
    }

    public ArrayList<Playable> getSortedProbabilities(Playable playable) {
        return getSortedProbabilities(firstMarkov[playable.getNumericRepresentation()], firstMarkovTotals[playable.getNumericRepresentation()]);
    }

    public ArrayList<Playable> getSortedProbabilities(Playable playable1, Playable playable2) {
        return getSortedProbabilities(secondMarkov[playable1.getNumericRepresentation()][playable2.getNumericRepresentation()], secondMarkovTotals[playable1.getNumericRepresentation()][playable2.getNumericRepresentation()]);
    }

    public ArrayList<Playable> getSortedProbabilities(Playable playable1, Playable playable2, Playable playable3) {
        return getSortedProbabilities(thirdMarkov[playable1.getNumericRepresentation()][playable2.getNumericRepresentation()][playable3.getNumericRepresentation()], thirdMarkovTotals[playable1.getNumericRepresentation()][playable2.getNumericRepresentation()][playable3.getNumericRepresentation()]);
    }

    public ArrayList<Playable> getSortedProbabilities(int[] array, int total) {
        ArrayList<Playable> result = new ArrayList<>();
        for (int i = 0; i < markovSize; i++) {
            Playable newPlayable = Playable.getPlayableFromMarkovNumeric(i, playableType);
            //Initially set to 0 and check total to avoid divide by 0
            double playableProbability = 0;
            if (total > 0) {
                playableProbability = (double) array[i] / (double) total;
            }
            newPlayable.setProbability(playableProbability);
            result.add(newPlayable);
        }
        Collections.sort(result);
        return result;
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
