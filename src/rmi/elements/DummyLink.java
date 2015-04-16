/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.elements;

import rmi.dummies.AgentDummy;

/**
 *
 * @author Martin
 */
public class DummyLink {
    int ID;
    AgentDummy fromDummy;
    AgentDummy toDummy;
    
    //Maybe:
    public enum LinkType {
        //Nice!
        InterAIPerformerLink, HumanToPerformerLink, MonitorOwnLink
    };
    LinkType linkType;
    int strength; // From 0 to 10
}
