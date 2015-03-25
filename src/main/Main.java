package main;

import arpeggiator.Arpeggiator;
import elements.Chord;
import java.util.*;
import org.jfugue.player.Player;
import markov.MarkovModel;
import chordextract.ChordExtractor;
import elements.Playable;
import elements.Sequence;

public class Main {

    public static void main(String[] args) {

        //Extract chords
        ArrayList<Sequence> chords = ChordExtractor.extractChordsFromMidiFiles("D:\\Desktop\\Dissertation\\MIDI-Live\\");

        //Generate markov model
        MarkovModel markovModel = new MarkovModel(3, new Chord());
        markovModel.trainModel(chords);

        //Add chords to live sequence
        markovModel.livePush(new Chord("G", "maj"));
        markovModel.livePush(new Chord("F", "maj"));
        markovModel.livePush(new Chord("C", "maj"));
        markovModel.livePush(new Chord("A", "min"));

        //Print probabilities
        markovModel.printSortedPlayables(3);

        //Print table
        //System.out.println(markovModel.toString());
        
        //Print table size
        System.out.println("Model length: " + markovModel.getTableSize());
        
        // Test Markov
        markovModel.testMethod();

        //Todo: Make player accept sequence objects.
        /*
         Player player = new Player();
         Arpeggiator arp = new Arpeggiator(player);
         for (Playable playable : markovOutput) {
         arp.addArpeggio((Chord) playable);
         }
         arp.play();
         */

    }
}
