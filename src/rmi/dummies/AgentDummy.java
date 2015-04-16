/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.dummies;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Martin
 */
public abstract class AgentDummy extends JPanel {
    String name;
    InetAddress address;
    int port;
    int ID;
    Point position;
    
    
    
    public AgentDummy(String name, int ID, InetAddress address) {
        this.name = name;
        this.ID = ID;
        this.address = address;
        
        this.setSize(32, 32);
    }
    
    public abstract String getIconFilename();
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("resources/" + getIconFilename() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(image, 0, 0, null);
    }
}
