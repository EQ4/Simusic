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
        ArrayList<Sequence> chords = ChordExtractor.extractChordsFromMidiFiles("D:\\Desktop\\Dissertation\\MIDI-Live\\");

        MarkovModel markovModel = new MarkovModel((ArrayList<Sequence>) chords, Playable.Type.CHORD);
        Sequence markovOutput = markovModel.getTestSequence();




        //Get possibilities for a next chord after F major and C major
        Playable chord1 = new Chord("F", "maj");
        Playable chord2 = new Chord("C", "maj");
        ArrayList<Playable> possibleChordChoices = markovModel.getSortedProbabilities(chord1, chord2);
        for (int i = 0; i < possibleChordChoices.size(); i++) {
            System.out.println(((Chord) possibleChordChoices.get(i)).getNameAndProbability());
        }


        System.out.println(markovModel.printFirstMarkovTable());
        System.out.println(markovModel.getProbabilityTableAfterPlayables(chord1, chord2));
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
