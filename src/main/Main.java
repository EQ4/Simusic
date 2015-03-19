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

    public static final String mainKey = "C";

    public static void main(String[] args) {
        ChordExtractor extractor = new ChordExtractor();
        ArrayList<ArrayList<Chord>> chords = extractor.extractChordsFromMidiFiles("D:\\Desktop\\Dissertation\\MIDI-Live\\");
        
        MarkovChain markovChain = new MarkovChain(chords);
        ArrayList<Chord> markovChords = markovChain.getTestSequence();
        
        System.out.println(markovChain.printFirstMarkovTable());
        System.out.println(markovChain.getProbability(new Chord("C", "maj"), new Chord("F", "maj")));
        
        // System.out.println(markovChain.printAllInputSequeces());
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
