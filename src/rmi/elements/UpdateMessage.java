/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.elements;

import java.util.ArrayList;
import rmi.dummies.AgentDummy;

/**
 *
 * @author Martin
 */
public class UpdateMessage {
    ArrayList<AgentDummy> updatedDummies;
    ArrayList<DummyLink> updatedLinks;
    
    public UpdateMessage() {
        updatedDummies = new ArrayList<>();
        updatedLinks = new ArrayList<>();
    }
    
    public void addUpdatedDummy(AgentDummy newDummy) {
        updatedDummies.add(newDummy);
    }
    
    public void addUpdatedLink(DummyLink newLink) {
        updatedLinks.add(newLink);
    }
    
    
    public ArrayList<AgentDummy> getUpdatedDummies() {
        return updatedDummies;
    }
    
    public ArrayList<DummyLink> getUpdatedLinks() {
        return updatedLinks;
    }
}
