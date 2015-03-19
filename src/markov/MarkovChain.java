/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package markov;

import elements.Chord;
import java.io.*;
import java.util.*;

/**
 *
 * @author Martin
 */
public class MarkovChain {

    public static final int markovSize = 24;
    public static final int initialMarkovValue = 0;
    private ArrayList<ArrayList<Chord>> inputChords;
    private int[] zeroMarkov;
    private int[][] firstMarkov;
    private int[][][] secondMarkov;
    private int[][][][] thirdMarkov;
    private int zeroMarkovTotal;
    private int[] firstMarkovTotals;
    private int[][] secondMarkovTotals;
    private int[][][] thirdMarkovTotals;

    public MarkovChain(ArrayList<ArrayList<Chord>> inputChords) {
        this.inputChords = inputChords;

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
                firstMarkov[i][j] = initialMarkovValue;
                secondMarkov[i][j] = new int[markovSize];
                secondMarkovTotals[i][j] = 0;
                for (int k = 0; k < markovSize; k++) {
                    secondMarkov[i][j][k] = initialMarkovValue;
                    thirdMarkov[i][j][k] = new int[markovSize];
                    thirdMarkovTotals[i][j][k] = 0;
                    for (int l = 0; l < markovSize; l++) {
                        thirdMarkov[i][j][k][l] = initialMarkovValue;
                    }
                }
            }
        }
    }

    private void generateMarkov() {
        for (int i = 0; i < inputChords.size(); i++) {
            ArrayList<Chord> subList = inputChords.get(i);
            for (int j = 0; j < subList.size(); j++) {
                Chord chord = subList.get(j);
                if (j == 0) {
                    zeroMarkov[chord.getMarkovNumeric()]++;
                    zeroMarkovTotal++;
                }
                if (j > 0) {
                    Chord previousChord = subList.get(j - 1);
                    firstMarkov[previousChord.getMarkovNumeric()][chord.getMarkovNumeric()]++;
                    firstMarkovTotals[previousChord.getMarkovNumeric()]++;
                    if (j > 1) {
                        Chord prePreviousChord = subList.get(j - 2);
                        secondMarkov[prePreviousChord.getMarkovNumeric()][previousChord.getMarkovNumeric()][chord.getMarkovNumeric()]++;
                        secondMarkovTotals[prePreviousChord.getMarkovNumeric()][previousChord.getMarkovNumeric()]++;
                        if (j > 2) {
                            Chord prePrePreviousChord = subList.get(j - 3);
                            thirdMarkov[prePrePreviousChord.getMarkovNumeric()][prePreviousChord.getMarkovNumeric()][previousChord.getMarkovNumeric()][chord.getMarkovNumeric()]++;
                            thirdMarkovTotals[prePrePreviousChord.getMarkovNumeric()][prePreviousChord.getMarkovNumeric()][previousChord.getMarkovNumeric()]++;
                        }
                    }
                }
            }
        }
    }

    public String printFirstMarkovTable() {
        String result = "First Markov table:\n***\tTotal\t";

        for (int i = 0; i < markovSize; i++) {
            result += Chord.getChordFromMarkovNumeric(i).getFullName() + "\t";
        }
        result += "\n";
        for (int i = 0; i < markovSize; i++) {
            result += Chord.getChordFromMarkovNumeric(i).getFullName() + "\t";
            result += firstMarkovTotals[i] + "\t";
            for (int j = 0; j < markovSize; j++) {
                result += firstMarkov[i][j] + "\t";
            }
            result += "\n";
        }
        return result;
    }

    public double getProbability(Chord chord1, Chord chord2) {
        int chord1Num = chord1.getMarkovNumeric();
        int chord2Num = chord2.getMarkovNumeric();
        if (firstMarkovTotals[chord1Num] == 0) {
            return 0;
        }
        return (double) firstMarkov[chord1Num][chord2Num] / (double) firstMarkovTotals[chord1Num];
    }

    public ArrayList<Chord> getTestSequence() {
        // TODO!

        return getAllInputSequences();
    }

    public ArrayList<Chord> getAllInputSequences() {
        ArrayList<Chord> outputChords = new ArrayList<>();
        for (int i = 0; i < inputChords.size(); i++) {
            ArrayList<Chord> subList = inputChords.get(i);
            for (int j = 0; j < subList.size(); j++) {
                outputChords.add(subList.get(j));
            }
        }
        return outputChords;
    }

    public ArrayList<Chord> getInputSequence(int number) {
        ArrayList<Chord> outputChords = new ArrayList<>();
        ArrayList<Chord> subList = inputChords.get(number);
        for (int i = 0; i < subList.size(); i++) {
            outputChords.add(subList.get(i));
        }
        return outputChords;
    }

    public String printAllInputSequeces() {
        String result = "Input sequences:\n";
        for (int i = 0; i < inputChords.size(); i++) {
            ArrayList<Chord> subList = inputChords.get(i);
            result += i + ": ";
            for (int j = 0; j < subList.size(); j++) {
                result += subList.get(j).getFullName() + " ";
            }
            result += "<End>\n";
        }
        return result;
    }
}
