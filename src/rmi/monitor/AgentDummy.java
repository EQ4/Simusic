/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.monitor;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import rmi.misc.AgentType;
import rmi.misc.AgentType;
import run.Main;

/**
 *
 * @author Martin
 */
public class AgentDummy extends JPanel implements Serializable {
    public static final int CANVAS_MAX_X = 500;
    public static final int CANVAS_MAX_Y = 500;
    
    AgentType agentType;
    String name;
    String address;
    int port;
    int ID;
    Point position;
    Integer masterMonitorID;
    
    boolean isOffline;
    
    
    public AgentDummy(AgentType agentType, String name, int ID, String address, int port, Integer masterMonitorID) {
        this.agentType = agentType;
        this.name = name;
        this.ID = ID;
        this.address = address;
        this.port = port;
        this.isOffline = false;
        this.masterMonitorID = masterMonitorID;
        this.position = new Point(Main.rand.nextInt(CANVAS_MAX_X), Main.rand.nextInt(CANVAS_MAX_Y));
        
        //Set icon image using getIconFilename ?
        this.setSize(32, 32);
    }
    
    public String getIconFilename() {
        switch (agentType) {
                case AIPerformer:
                    return "aiperformer";
                case HumanPerformer:
                    return "humanperformer";
                case Monitor:
                    return "monitor";
            }
        return null;
    };
    
    public void disconnect() {
        this.isOffline = true;
    }
    
    public boolean isOffline() {
        return isOffline;
    }
    
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
