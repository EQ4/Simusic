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
        int id = frame.agentConnections.size();
        AgentDummy newAgentDummy = new AgentDummy(agentType, agentName, id, agentIP, agentPort, masterMonitorID);
        frame.agentDummies.add(newAgentDummy);
        try {
            frame.agentConnections.add((AgentInterface) Naming.lookup("rmi://" + agentIP + ":" + agentPort + "/" + agentName));
        } catch (Exception e) {
            System.out.println("Registry to agent connection exception: " + e.getMessage());
            e.printStackTrace();
        }
        log(agentType + " has connected! Assigning id = " + id);
        
        //Say hello
        frame.agentConnections.get(id).sayHello();
        
        broadcastUpdates();
        
        return getFullUpdate();
    }
    
    public void broadcastUpdates() throws RemoteException {
        for (AgentInterface agent : frame.agentConnections) {
            agent.update(getFullUpdate());
        }
    }
    
    private UpdateMessage getFullUpdate() {
        UpdateMessage result = new UpdateMessage();
        result.updatedDummies = frame.agentDummies;
        return result;
    }

    @Override
    public boolean ping(int id) {
        log("Agent " + id + " pinged and got ponged!");
        return true;
    }

    @Override
    public boolean disconnect(int id) {
        log("Agent " + id + " has disconnected!");
        return true;
    }

    @Override
    public String sayHello(int id) {
        log("Agent " + id + " says hi!");
        return "Hello from registry!";
    }

    private void log(String message) {
        frame.log(message);
    }

}
