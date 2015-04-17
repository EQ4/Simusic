/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.monitor;

import java.io.Serializable;
import rmi.monitor.AgentDummyLink;
import java.util.ArrayList;

/**
 *
 * @author Martin
 */
public class UpdateMessage implements Serializable {
    public ArrayList<AgentDummy> updatedDummies;
    public ArrayList<AgentDummyLink> updatedLinks;
    public AgentDummy welcomePack;
    public boolean shutDown;
    
    public UpdateMessage() {
        updatedDummies = new ArrayList<>();
        updatedLinks = new ArrayList<>();
    }
    
    public void addUpdatedDummy(AgentDummy newDummy) {
        updatedDummies.add(newDummy);
    }
    
    public void addUpdatedLink(AgentDummyLink newLink) {
        updatedLinks.add(newLink);
    }
    
    
    public ArrayList<AgentDummy> getUpdatedDummies() {
        return updatedDummies;
    }
    
    public ArrayList<AgentDummyLink> getUpdatedLinks() {
        return updatedLinks;
    }
}
