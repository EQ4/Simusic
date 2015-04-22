package run;

import java.io.File;
import music.elements.Chord;
import java.util.*;
import music.markov.MarkovModel;
import music.extractors.chord.ChordExtractor;
import music.elements.Playable;
import music.elements.Sequence;
import music.extractors.feature.FeatureExtractor;
import java.io.FileInputStream;
import java.io.InputStream;
import music.player.Player;
import static run.Main.player;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Test {

    public static String clickPath = "E:\\Desktop\\Dissertation\\_runtime\\click_sounds\\";

    //Obsolete
    static String midiPath = "E:\\Desktop\\Dissertation\\_runtime\\midi_full\\";
    static String featurePath = "E:\\Desktop\\Dissertation\\_runtime\\features\\";
    //Used for test class
    static ArrayList<Sequence> chords;
    static MarkovModel markovModel;

    public static void main(String[] args) {
        
    }

    static void testMarkov() {
        //Train markov
        trainMarkov();

        //Add chords to live sequence
        while (true) {
            Playable nextChord = markovModel.getTopCondensedProcessedPlayable();
            markovModel.livePush(nextChord);
            System.out.println(nextChord.toString());
            Main.wait(400);
        }

        /*
        markovModel.livePush(new Chord("G", "maj"));
        markovModel.livePush(new Chord("F", "maj"));
        markovModel.livePush(new Chord("C", "maj"));
        markovModel.livePush(new Chord("A", "min"));
        markovModel.livePush(new Chord("E", "maj"));
        markovModel.livePush(new Chord("F", "maj"));
        markovModel.livePush(new Chord("G", "maj"));

        //Print condensed probabilities
        System.out.println(markovModel.getCondensedSortedProbabilityString());
                
                */
    }

    static void testFeatures() {
        //Extract features
        //FeatureExtractor fextract = new FeatureExtractor(midiPath, featurePath, true);

        //Print all average features
        //fextract.printAverageFeatures();
    }

    static void testPlayer() {
        //Train markov
        trainMarkov();

    }

    static void trainMarkov() {
        //Extract chords
        extractChords();

        //Train markov model
        markovModel = new MarkovModel(3, new Chord());
        markovModel.trainModel(chords);
        System.out.println(markovModel.toString());
    }

    static void extractChords() {
        chords = ChordExtractor.extractChordsFromMidiFiles(new File(midiPath).listFiles(), null);
    }
}
