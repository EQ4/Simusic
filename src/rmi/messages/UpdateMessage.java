/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.messages;

import java.io.Serializable;
import java.util.ArrayList;
import rmi.agents.monitor.AgentDummy;
import rmi.agents.monitor.AgentDummyLink;

/**
 *
 * @author Martin
 */
public class UpdateMessage implements Serializable {
    public ArrayList<AgentDummy> updatedDummies;
    public ArrayList<AgentDummyLink> updatedLinks;
    public AgentDummy welcomePack;
    public boolean shutDown;
    
    public UpdateMessage(ArrayList<AgentDummy> updatedDummies, ArrayList<AgentDummyLink> updatedLinks) {
        this.updatedDummies = updatedDummies;
        this.updatedLinks = updatedLinks;
    }
}
