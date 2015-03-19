package main;

import arpeggiator.Arpeggiator;
import chordextract.ChordScanner;
import elements.Chord;
import jm.JMC;
import jm.music.data.*;
import jm.music.tools.*;
import jm.util.*;
import java.util.Vector;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import org.jfugue.player.Player;
import markov.MarkovChain;
import services.Services;
import elements.Note;
import chordextract.ChordExtractor;

public class Main {

    public static void main(String[] args) {
        ArrayList<ArrayList<Chord>> chords = ChordExtractor.extractChordsFromMidiFiles("D:\\Desktop\\Dissertation\\MIDI-Live\\");

        MarkovChain markovModel = new MarkovChain(chords);
        ArrayList<Chord> markovChords = markovModel.getTestSequence();




        //Get possibilities for a next chord after F major and C major
        Chord chord1 = new Chord("F", "maj");
        Chord chord2 = new Chord("C", "maj");
        ArrayList<Chord> possibleChordChoices = markovModel.getSortedProbabilities(chord1, chord2);
        for (int i = 0; i < possibleChordChoices.size(); i++) {
            System.out.println(possibleChordChoices.get(i).getNameAndProbability());
        }


        //System.out.println(markovModel.printFirstMarkovTable());
        //System.out.println(markovModel.getProbabilityTableAfterChords(chord1, chord2));
        //System.out.println(markovModel.printAllInputSequeces());
        System.exit(1);





        Player player = new Player();
        Arpeggiator arp = new Arpeggiator(player);
        for (int i = 0; i < markovChords.size(); i++) {
            Chord currentChord = markovChords.get(i);
            arp.addArpeggio(currentChord);
        }
        arp.play();

    }
    //Sequence is naive.
}
