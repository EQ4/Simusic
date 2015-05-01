/*
 * The MIT License
 *
 * Copyright 2015 Martin Minovski <martin at minovski.net>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package rmi.agents.monitor;

import java.io.Serializable;
import enums.AgentType;

/**
 * Class for creating objects which represent the agents in GUI
 * @author Martin Minovski <martin at minovski.net>
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

    /**
     * Default constructor
     * @param agentType The agent type
     * @param name The agent name
     * @param ID Agent ID in Registry
     * @param ip Agent IP
     * @param port Agent port
     * @param masterMonitorID Master monitor ID
     */
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

    /**
     * Returns the icon filename
     * @return
     */
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
    
    /**
     * Returns the RMI address
     * @return
     */
    public String getRMIAddress() {
        return "rmi://" + ip + ":" + port + "/" + name;
    }

    /**
     * Indicates the agent is offline.
     */
    public void disconnect() {
        this.isOffline = true;
    }
}
