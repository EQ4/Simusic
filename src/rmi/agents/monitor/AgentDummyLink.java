/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.agents.monitor;

import java.io.Serializable;

/**
 *
 * @author Martin
 */
public class AgentDummyLink implements Serializable {
    
    AgentLinkType linkType;
    int ID;
    int fromAgentID;
    int toAgentID;
    
    //Not used yet, just ideas
    boolean isActive = true;
    int strength = 1;
    
    public AgentDummyLink(AgentLinkType linkType, int ID, int fromAgentID, int toAgentID) {
        this.linkType = linkType;
        this.ID = ID;
        this.fromAgentID = fromAgentID;
        this.toAgentID = toAgentID;
    }
    
    
    public enum AgentLinkType {
        //Nice!
        AINeighbourLink, HumanToPerformerLink, MonitorOwnLink
    };
}
