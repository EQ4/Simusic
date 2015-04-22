/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package music.player;

import javax.sound.midi.MidiUnavailableException;
import music.elements.Chord;
import org.jfugue.realtime.RealtimePlayer;

/**
 *
 * @author Martin
 */
public class Player {

    RealtimePlayer player;

    public Player() {
        try {
            player = new RealtimePlayer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playArpeggio(Chord chord, int timeFrame) {
        //TODO: Implement
    }
}
