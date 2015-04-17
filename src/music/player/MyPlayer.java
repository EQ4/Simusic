/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package music.player;

import org.jfugue.player.Player;

/**
 *
 * @author Martin
 */
public class MyPlayer {
    
    public MyPlayer () {
        
    }
    
    public void play(String sequence) {
        Player player = new Player();
        player.play(sequence);
        
    }
}
