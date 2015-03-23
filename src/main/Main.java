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

    public static final int markovDepth = 3;

    public static void main(String[] args) {
        ArrayList<Sequence> chords = ChordExtractor.extractChordsFromMidiFiles("D:\\Desktop\\Dissertation\\MIDI-Live\\");

        MarkovModel markovModel = new MarkovModel(markovDepth, Playable.Type.CHORD);
        markovModel.trainModel(chords);
        Sequence markovOutput = markovModel.getTestSequence();



        for (Playable playable : markovOutput.getSequence()) {
            //System.out.println(((Chord) playable).getNameAndProbability());
        }

        System.out.println(markovModel.printAllInputSequeces());
        
        System.exit(1);




        //Todo: Make player accept sequence objects.
        Player player = new Player();
        Arpeggiator arp = new Arpeggiator(player);
        for (Playable playable : markovOutput.getSequence()) {
            arp.addArpeggio((Chord) playable);
        }
        arp.play();

    }
}
