/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.agents.monitor;

import java.io.Serializable;
import enums.AgentType;

/**
 *
 * @author Martin
 */
public class AgentDummy implements Serializable {

    public AgentType agentType;
    public String name;
    public String ip;
    public int port;
    public int agentID;
    public Integer masterMonitorID;
    public int latency;
    public boolean isReady;
    public boolean isOffline;

    public boolean isChordWinner;
    public boolean isSoloing;

    public boolean isConductor;
    public boolean isLeafAgent;

    //Feature/role model vars
    public Double[] features;
    public String roleModelMessage;

    public AgentDummy(AgentType agentType, String name, int ID, String ip, int port, Integer masterMonitorID) {
        this.agentType = agentType;
        this.name = name;
        this.agentID = ID;
        this.ip = ip;
        this.port = port;
        this.isOffline = false;
        this.masterMonitorID = masterMonitorID;
        this.isReady = true;

        this.roleModelMessage = "";

        this.isChordWinner = false;
        this.isSoloing = false;

        this.isConductor = false;
        this.isLeafAgent = false;

        if (agentType == AgentType.AIPerformer) {
            this.isReady = false;
            this.isLeafAgent = true;
        }
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
    }

    ;
    
    public String getRMIAddress() {
        return "rmi://" + ip + ":" + port + "/" + name;
    }

    public void disconnect() {
        this.isOffline = true;
    }
}
