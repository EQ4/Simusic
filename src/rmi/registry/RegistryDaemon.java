/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.registry;

import static java.lang.Math.abs;
import rmi.monitor.UpdateMessage;
import rmi.interfaces.RegistryInterface;
import java.rmi.Naming;
import java.net.Inet4Address;
import java.rmi.server.UnicastRemoteObject;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.util.ArrayList;
import javax.swing.JTextArea;
import rmi.monitor.AgentDummy;
import rmi.interfaces.AgentInterface;
import run.Main;
import rmi.misc.AgentType;
import rmi.monitor.AgentDummyLink;
import rmi.monitor.AgentDummyLink.AgentLinkType;

/**
 *
 * @author Martin
 */
public class RegistryDaemon extends UnicastRemoteObject implements RegistryInterface {
    static final int LATENCY_PINGS_PER_AGENT = 10;
    static final int DELAY_BETWEEN_LATENCY_PINGS = 100;
    
    ArrayList<AgentDummy> agentFeatureStorage;

    RegistryFrame frame;
    boolean sessionLocked;

    public RegistryDaemon(RegistryFrame frame) throws RemoteException {
        super(frame.registryServicePort);
        this.frame = frame;
        sessionLocked = false;
        this.agentFeatureStorage = new ArrayList<>();
    }

    @Override
    public synchronized UpdateMessage connect(AgentType agentType, String agentName, String agentIP, int agentPort, Integer masterMonitorID) throws RemoteException {
        //If performance has started, no more agents can connect
        if (sessionLocked) {
            return null;
        }
        
        int id = frame.agentDummies.size();
        AgentDummy newAgentDummy = new AgentDummy(agentType, agentName, id, agentIP, agentPort, masterMonitorID);
        AgentInterface agentConnection;
        try {
            agentConnection = (AgentInterface) Naming.lookup("rmi://" + agentIP + ":" + agentPort + "/" + agentName);
        } catch (Exception e) {
            System.out.println("Registry to agent connection exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        //Measure latency
        newAgentDummy.latency = measureAgentLatency(agentConnection);
        frame.agentDummies.add(newAgentDummy);
        
        frame.log("A new agent has connected!\n"
                + "    - name: " + agentName + "\n"
                + "    - type: " + agentType.toString() + "\n"
                + "    - ip: " + agentIP + "\n"
                + "    - port: " + agentName + "\n"
                + "    - latency: " + newAgentDummy.latency + "\n"
                + "    - ID: " + id + "\n"
                + "    - owned by: " + masterMonitorID
        );

        //Say hello
        agentConnection.sayHello();
        
        //Store connection
        frame.agentConnections.add(agentConnection);

        //Update agents
        triggerGlobalUpdate();

        return getFirstUpdate(newAgentDummy);
    }

    public void triggerGlobalUpdate() throws RemoteException {
        for (AgentInterface agent : frame.agentConnections) {
            if (agent != null) {
                agent.update(getRegularUpdate());
            }
        }
    }

    private UpdateMessage getRegularUpdate() {
        UpdateMessage result = new UpdateMessage(frame.agentDummies, frame.agentDummyLinks);
        return result;
    }

    private UpdateMessage getFirstUpdate(AgentDummy newAgentDummy) {
        UpdateMessage result = getRegularUpdate();
        result.welcomePack = newAgentDummy;
        return result;
    }
    
    private int measureAgentLatency(AgentInterface agentConnection) throws RemoteException {
        int latencySum = 0;
        long timeOnPingSend;
        for (int i = 0; i < LATENCY_PINGS_PER_AGENT; i++) {
            timeOnPingSend = System.currentTimeMillis();
            agentConnection.ping();
            latencySum += (System.currentTimeMillis() - timeOnPingSend);
            Main.wait(DELAY_BETWEEN_LATENCY_PINGS);
        }
        return (latencySum / LATENCY_PINGS_PER_AGENT) / 2;
    }

    @Override
    public boolean ping(int id) throws RemoteException {
        frame.log("Agent " + id + " pinged and got ponged!");
        return true;
    }

    @Override
    public synchronized boolean disconnect(int id) throws RemoteException {
        AgentDummy agent = frame.agentDummies.get(id);
        frame.agentConnections.set(id, null);
        frame.log(agent.name + " (#" + id + ") has disconnected.");
        agent.isOffline = true;
        
        //Update agents
        triggerGlobalUpdate();
        
        frame.log(agent.agentType + " " + agent.name + " has disconnected.");
        return true;
    }

    @Override
    public String sayHello(String sender) throws RemoteException {
        frame.log(sender + " says hi!");
        return "Hello from registry!";
    }

    @Override
    public void log(String message, int loggerID) throws RemoteException {
        frame.log("<" + frame.agentDummies.get(loggerID).name + "> " + message);
    }
    
    @Override
    public AgentDummy getAgentDummyByID(int agentID) {
        return frame.agentDummies.get(agentID);
    }
    
    @Override
    public void reportNeighbourConnection(int fromAgentID, int toAgentID) throws RemoteException {
        int newLinkID = frame.agentDummyLinks.size();
        AgentDummyLink newAgentLink = new AgentDummyLink(AgentLinkType.AINeighbourLink, newLinkID, fromAgentID, toAgentID);
        frame.agentDummyLinks.add(newAgentLink);
        
        //Update agents
        triggerGlobalUpdate();
    }
    
    @Override
    public void agentLoaded(int agentID) throws RemoteException {
        frame.agentDummies.get(agentID).isReady = true;
        triggerGlobalUpdate();
    }
    
    @Override
    public AgentDummy getRoleModel(int agentID, Double[] featureValues) throws RemoteException {
        AgentDummy roleModel = null;
        double minDistance = Double.MAX_VALUE;
        for (AgentDummy agent : agentFeatureStorage) {
            double distanceToAgent = 0;
            for (int i = 0; i < featureValues.length; i++) {
                distanceToAgent += abs(featureValues[i] - agent.features[i]);
            }
            if (distanceToAgent < minDistance) {
                minDistance = distanceToAgent;
                roleModel = agent;
                agent.roleModelMessage = minDistance + "";
            }
        }
        //Store this agent
        AgentDummy newRoleModel = frame.agentDummies.get(agentID);
        newRoleModel.features = featureValues;
        agentFeatureStorage.add(newRoleModel);
        return roleModel;
    }
    
    @Override
    public String startPerformance() throws RemoteException {
        //Check if there is at least one AI agent
        for (int i = 0; i < frame.agentDummies.size(); i++) {
            if (frame.agentDummies.get(i).agentType == AgentType.AIPerformer) {
                //All good - continue
                break;
            }
            if (i == frame.agentDummies.size() - 1) {
                return "There are no AI Performers!";
            }
        }
        
        //Ensure that no agent is loading.
        for (AgentDummy agentDummy : frame.agentDummies) {
            if (!agentDummy.isReady) {
                return "One or more agents are still loading!";
            }
        }
        
        //No more agents can connect
        sessionLocked = true;
        
        //TODO: Take ping into consideration?
        
        //Start performance:
        for (AgentInterface agentConnection : frame.agentConnections) {
            agentConnection.startPerformance();
        }
        
        frame.log("Performance started!");
        
        return null;
    }
    
    @Override
    public boolean isPerforming() throws RemoteException {
        return sessionLocked;
    }
    
}
