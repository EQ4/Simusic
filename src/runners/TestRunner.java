package runners;

import agents.AgentManager;
import player.Arpeggiator;
import elements.Chord;
import java.util.*;
import org.jfugue.player.Player;
import markov.MarkovModel;
import extractors.chord.ChordExtractor;
import elements.Playable;
import elements.Sequence;
import extractors.feature.FeatureExtractor;
import java.io.FileInputStream;
import java.io.InputStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class TestRunner {

    public static String clickPath = "E:\\Desktop\\Dissertation\\_runtime\\click_sounds\\";
    
    //Obsolete
    static String midiPath = "E:\\Desktop\\Dissertation\\_runtime\\midi_mid\\";
    static String featurePath = "E:\\Desktop\\Dissertation\\_runtime\\features\\";
    //Used for test class
    static ArrayList<Sequence> chords;
    static MarkovModel markovModel;

    public static void main(String[] args) {
        //Deprecated:
        //testMarkov();
        //testFeatures();
        //testPlayer();
    }

    static void testMarkov() {
        //Train markov
        trainMarkov();

        //Add chords to live sequence
        markovModel.livePush(new Chord("G", "maj"));
        markovModel.livePush(new Chord("F", "maj"));
        markovModel.livePush(new Chord("C", "maj"));
        markovModel.livePush(new Chord("A", "min"));

        //Print probabilities
        markovModel.printSortedPlayables(3);

        //Print table
        System.out.println(markovModel.toString());

        //Print table size
        System.out.println("Model length: " + markovModel.getTableSize());

        // Test Markov
        markovModel.testMethod();
    }

    static void testFeatures() {
        //Extract features
        FeatureExtractor fextract = new FeatureExtractor(midiPath, featurePath, true);

        //Print all average features
        fextract.printAverageFeatures();
    }

    static void testPlayer() {
        //Train markov
        trainMarkov();

        //Arpeggiate all markov inputs
        Player player = new Player();
        Arpeggiator arp = new Arpeggiator(player);
        for (Playable playable : markovModel.getAllInputSequences()) {
            arp.addArpeggio((Chord) playable);
        }
        arp.play();
    }

    static void trainMarkov() {
        //Extract chords
        extractChords();

        //Train markov model
        markovModel = new MarkovModel(3, new Chord());
        markovModel.trainModel(chords);
    }

    static void extractChords() {
        chords = ChordExtractor.extractChordsFromMidiFiles(midiPath);
    }
}
