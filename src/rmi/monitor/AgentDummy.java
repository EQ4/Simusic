/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.monitor;

import java.io.Serializable;
import rmi.misc.AgentType;

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
        if (agentType == AgentType.AIPerformer) {
            this.isReady = false;
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
