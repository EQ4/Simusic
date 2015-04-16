/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.registry;

import rmi.elements.UpdateMessage;
import rmi.interfaces.MonitorInterface;
import rmi.interfaces.AiPerformerInterface;
import rmi.interfaces.HumanPerformerInterface;
import java.rmi.Naming;
import java.lang.SecurityManager;
import java.rmi.server.UnicastRemoteObject;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 *
 * @author Martin
 */
public class Registry extends UnicastRemoteObject implements AiPerformerInterface, HumanPerformerInterface, MonitorInterface {
    public static int MAX_REGISTRY_ID = 100;
    public static enum AgentType {

        AIPerformer, HumanPerformer, Monitor
    }
    
    
    public String name;
    JTextArea registryLog;

    public Registry(String name, JTextArea registryLog) throws RemoteException {
        this.name = name;
        this.registryLog = registryLog;

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
    public UpdateMessage getFullUpdate() {
        //TODO: Implement
        return null;
    }

    @Override
    public UpdateMessage getPartialUpdate() {
        //TODO: Implement
        return null;
    }
    
    @Override
    public int connect(AgentType agentType) {
        int id = -1;
        log(agentType + " has connected! Assigning id = " + id);
        return id;
    }
    
    @Override
    public boolean ping(int id) {
        log("Agent " + id + " pings! Ponging...");
        return true;
    }
    
    @Override
    public boolean disconnect(int id) {
        log("Agent " + id + " has disconnected!");
        return true;
    }
    
    @Override
    public String sayHello() {
        log("Someone made me say hello!");
        return "Hello from registry!";
    }
    
    private void log(String message) {
        registryLog.append(message + "\n");
    }
}
