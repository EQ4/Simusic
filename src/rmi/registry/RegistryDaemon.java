/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.registry;

import rmi.monitor.UpdateMessage;
import rmi.interfaces.RegistryInterface;
import java.rmi.Naming;
import java.lang.SecurityManager;
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

/**
 *
 * @author Martin
 */
public class RegistryDaemon extends UnicastRemoteObject implements RegistryInterface {

    RegistryFrame frame;

    public RegistryDaemon(RegistryFrame frame) throws RemoteException {
        super(frame.registryServicePort);
        this.frame = frame;
        System.setProperty("java.security.policy", "simusic.policy");
        System.setSecurityManager(new SecurityManager());
    }

    @Override
    public void broadcast(String message) {
        //TODO: Implement
    }

    @Override
    public void unicast(String message, String recipient) {
        //TODO: Implement
    }

    @Override
    public void multicast(String message, ArrayList<String> recipients) {
        //TODO: Implement
    }

    @Override
    public void noteOn(int pitch, int velocity) {
        //TODO: Implement
    }

    @Override
    public void noteOff(int pitch) {
        //TODO: Implement
    }

    @Override
    public void sustainOn() {
        //TODO: Implement
    }

    @Override
    public void sustainOff() {
        //TODO: Implement
    }

    @Override
    public UpdateMessage connect(AgentType agentType, String agentName, String agentIP, int agentPort, Integer masterMonitorID) throws RemoteException {
        int id = frame.agentDummies.size();
        AgentDummy newAgentDummy = new AgentDummy(agentType, agentName, id, agentIP, agentPort, masterMonitorID);
        frame.agentDummies.add(newAgentDummy);
        try {
            frame.agentConnections.add((AgentInterface) Naming.lookup("rmi://" + agentIP + ":" + agentPort + "/" + agentName));
        } catch (Exception e) {
            System.out.println("Registry to agent connection exception: " + e.getMessage());
            e.printStackTrace();
        }
        log("A new agent has connected!\n"
                + "    - name: " + agentName + "\n"
                + "    - type: " + agentType.toString() + "\n"
                + "    - ip: " + agentIP + "\n"
                + "    - port: " + agentName + "\n"
                + "    - ID: " + id + "\n"
                + "    - owned by: " + masterMonitorID
        );

        //Say hello
        frame.agentConnections.get(id).sayHello();

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
        UpdateMessage result = new UpdateMessage();
        result.updatedDummies = frame.agentDummies;
        return result;
    }

    private UpdateMessage getFirstUpdate(AgentDummy newAgentDummy) {
        UpdateMessage result = new UpdateMessage();
        result.updatedDummies = frame.agentDummies;
        result.welcomePack = newAgentDummy;
        return result;
    }

    @Override
    public boolean ping(int id) throws RemoteException {
        log("Agent " + id + " pinged and got ponged!");
        return true;
    }

    @Override
    public boolean disconnect(int id) throws RemoteException {
        AgentDummy agent = frame.agentDummies.get(id);
        frame.agentConnections.set(id, null);
        log(agent.name + " (#" + id + ") has disconnected.");
        if (agent.masterMonitorID == null) {
            for (AgentDummy dummy : frame.agentDummies) {
                if (dummy.masterMonitorID != null) {
                    if (dummy.masterMonitorID == id) {
                        dummy.isOffline = true;
                        log("    - " + dummy.name + " died because his master monitor left");
                    }
                }
            }
        }
        agent.isOffline = true;
        //Update agents
        triggerGlobalUpdate();
        return true;
    }

    @Override
    public String sayHello(int id) throws RemoteException {
        log("Agent " + id + " says hi!");
        return "Hello from registry!";
    }

    private void log(String message) throws RemoteException {
        frame.log(message);
    }
    
    @Override
    public AgentDummy getAgentDummyByID(int agentID) {
        return frame.agentDummies.get(agentID);
    }

}
