/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package music.player;

import music.elements.Chord;
import java.util.Random;
import org.jfugue.player.Player;

/**
 * TODO: DELETE
 * @author Martin
 */
public class Arpeggiator {

    Player player;
    String stringToPlay = "";
    int[] chordNum = {0, -1, 7}; //Only major and minor for now!

    public Arpeggiator(Player player) {
        this.player = player;
    }

    private String addNote(String note, int num) {
        String notes[] = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",};
        for (int i = 0; i < 12; i++) {
            if (note.equals(notes[i])) {
                return notes[(i + num) % 12];
            }
        }
        return "ERORROROOROR";
    }

    private int[] getNumbers(Chord chord) {


        //Major
        if (chord.isMajor()) {
            chordNum[1] = 4;
        }
        else {
            chordNum[1] = 3;
        }

        return chordNum;
    }

    public void playArpeggio(Chord chord, int timeFrame) {
        String base = chord.getBaseLetter();
        int[] numbers = getNumbers(chord);

        Random rand = new Random();

        
        stringToPlay += (base + "3q+"
                + base + "4q+"
                + (addNote(base, chordNum[rand.nextInt(3)]) + "6q")) + " ";


        stringToPlay += (addNote(base, numbers[0]) + "5qa56d56") + " ";
        stringToPlay += (addNote(base, numbers[1]) + "5qa56d56") + " ";
        stringToPlay += (addNote(base, numbers[2]) + "5qa56d56") + ("+" + addNote(base, chordNum[rand.nextInt(3)]) + "6q") + " ";
        stringToPlay += (addNote(base, numbers[1]) + "5qa56d56") + ("+" + addNote(base, chordNum[rand.nextInt(3)]) + "6q") + " ";
        stringToPlay += (addNote(base, numbers[0]) + "5qa56d56") + ("+" + addNote(base, chordNum[rand.nextInt(3)]) + "6q") + " ";

        
    }

    public void play() {
        player.play(stringToPlay);
    }
}
